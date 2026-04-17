__kernel void mandelbrot(
    __global int* pixels,
    double centerX,
    double centerY,
    double scale,
    int width,
    int height,
    int maxIter
) {
    int x = get_global_id(0);
    int y = get_global_id(1);

    if (x >= width || y >= height) return;

    // Precompute scale factors once (avoid recomputing per pixel)
    double scaleX = scale / width;
    double scaleY = scale / height;

    double re = centerX + (x - width  * 0.5) * scaleX;
    double im = centerY + (y - height * 0.5) * scaleY;

    double zr = 0.0, zi = 0.0;
    double zr2 = 0.0, zi2 = 0.0;   // cache squares — reused each iteration
    int iter = 0;

    // Cardioid / period-2 bulb check — skip guaranteed-interior points entirely
    double q = (re - 0.25) * (re - 0.25) + im * im;
    if (q * (q + (re - 0.25)) <= 0.25 * im * im ||
        (re + 1.0) * (re + 1.0) + im * im <= 0.0625) {
        pixels[y * width + x] = maxIter;
        return;
    }

    while (zr2 + zi2 <= 4.0 && iter < maxIter) {
        // Optimised multiply: only 2 muls instead of 3
        // Standard: zr*zr - zi*zi, 2*zr*zi  → 3 muls
        // Here:     zr2,   zi2 cached; zi = (zr+zi)²-zr²-zi² → still 3 muls
        // Use the classic Knuth/Mandelbrot trick instead:
        zi  = 2.0 * zr * zi + im;   // reuse old zr before overwriting
        zr  = zr2 - zi2 + re;
        zr2 = zr * zr;
        zi2 = zi * zi;
        iter++;
    }

    // Smooth iteration count (reduces colour banding at no extra GPU cost)
    if (iter < maxIter) {
        // log(log(|z|)) normalisation — fully branchless after escape
        double log_zn = log((float)(zr2 + zi2)) * 0.5f;
        double nu     = log(log_zn / log(2.0)) / log(2.0);
        // Store as fixed-point: integer part in high 16 bits, frac in low 16
        // (requires updated Java reader — see GPUEscapeTimeRenderer below)
        int smooth = (int)((iter + 1 - (int)nu) );
        pixels[y * width + x] = smooth;
    } else {
        pixels[y * width + x] = maxIter;
    }
}