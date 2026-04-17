package gpu;

import model.SceneSettings;
import org.jocl.*;

import static org.jocl.CL.*;

public class OpenCLContext {

    private cl_context context;
    private cl_command_queue queue;
    private cl_program program;
    private cl_kernel kernel;
    private cl_device_id device;

    // Reuse the pixel buffer across frames — only reallocate on resize
    private cl_mem pixelBuffer;
    private int allocatedSize = 0;

    public OpenCLContext() {
        init();
        buildKernel();
    }

    private void init() {
        CL.setExceptionsEnabled(true);

        cl_platform_id[] platforms = new cl_platform_id[1];
        clGetPlatformIDs(1, platforms, null);

        cl_device_id[] devices = new cl_device_id[1];
        clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_GPU, 1, devices, null);
        device = devices[0];

        cl_context_properties props = new cl_context_properties();
        props.addProperty(CL_CONTEXT_PLATFORM, platforms[0]);

        context = clCreateContext(props, 1, new cl_device_id[]{device},
                null, null, null);

        // Enable out-of-order execution if the device supports it
        long[] queueProps = new long[]{
                CL_QUEUE_PROPERTIES,
                0L,   // swap to CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE if needed
                0L
        };
        queue = clCreateCommandQueueWithProperties(context, device, null, null);
    }

    private void buildKernel() {
        // Build with aggressive optimisation flags
        String buildOptions = "-cl-fast-relaxed-math -cl-mad-enable -cl-no-signed-zeros";

        String source = """
                #pragma OPENCL EXTENSION cl_khr_fp64 : enable
                __kernel void mandelbrot(
                    __global int* pixels,
                    double centerX, double centerY, double scale,
                    int width, int height, int maxIter)
                {
                    int x = get_global_id(0);
                    int y = get_global_id(1);
                    if (x >= width || y >= height) return;

                    double scaleX = scale / width;
                    double scaleY = scale / height;
                    double re = centerX + (x - width  * 0.5) * scaleX;
                    double im = centerY + (y - height * 0.5) * scaleY;

                    double q = (re - 0.25) * (re - 0.25) + im * im;
                    if (q * (q + (re - 0.25)) <= 0.25 * im * im ||
                        (re + 1.0) * (re + 1.0) + im * im <= 0.0625) {
                        pixels[y * width + x] = maxIter;
                        return;
                    }

                    double zr = 0.0, zi = 0.0, zr2 = 0.0, zi2 = 0.0;
                    int iter = 0;
                    while (zr2 + zi2 <= 4.0 && iter < maxIter) {
                        zi  = 2.0 * zr * zi + im;
                        zr  = zr2 - zi2 + re;
                        zr2 = zr * zr;
                        zi2 = zi * zi;
                        iter++;
                    }
                    if (iter < maxIter) {
                        double log_zn = log((float)(zr2 + zi2)) * 0.5f;
                        double nu     = log(log_zn / log(2.0)) / log(2.0);
                        pixels[y * width + x] = iter + 1 - (int)nu;
                    } else {
                        pixels[y * width + x] = maxIter;
                    }
                }
                """;

        program = clCreateProgramWithSource(context, 1, new String[]{source}, null, null);

        int err = clBuildProgram(program, 0, null, buildOptions, null, null);
        if (err != CL_SUCCESS) {
            printBuildLog();
            throw new RuntimeException("OpenCL build failed");
        }

        kernel = clCreateKernel(program, "mandelbrot", null);
    }

    public void runMandelbrot(int[] pixels, SceneSettings s, int maxIter) {
        int size = pixels.length;

        if (size != allocatedSize) {
            if (pixelBuffer != null) clReleaseMemObject(pixelBuffer);
            pixelBuffer = clCreateBuffer(context, CL_MEM_WRITE_ONLY,
                    (long) Sizeof.cl_int * size, null, null);
            allocatedSize = size;
        }

        clSetKernelArg(kernel, 0, Sizeof.cl_mem,   Pointer.to(pixelBuffer));
        clSetKernelArg(kernel, 1, Sizeof.cl_double, Pointer.to(new double[]{s.centerX}));
        clSetKernelArg(kernel, 2, Sizeof.cl_double, Pointer.to(new double[]{s.centerY}));
        clSetKernelArg(kernel, 3, Sizeof.cl_double, Pointer.to(new double[]{s.scale}));
        clSetKernelArg(kernel, 4, Sizeof.cl_int,    Pointer.to(new int[]{s.width}));
        clSetKernelArg(kernel, 5, Sizeof.cl_int,    Pointer.to(new int[]{s.height}));
        clSetKernelArg(kernel, 6, Sizeof.cl_int,    Pointer.to(new int[]{maxIter}));

        final long LOCAL = 16;

        // Round UP so globalSize is always a multiple of LOCAL
        long gx = ((s.width  + LOCAL - 1) / LOCAL) * LOCAL;
        long gy = ((s.height + LOCAL - 1) / LOCAL) * LOCAL;

        long[] globalSize = {gx, gy};
        long[] localSize  = {LOCAL, LOCAL};

        clEnqueueNDRangeKernel(queue, kernel, 2, null, globalSize, localSize,
                0, null, null);

        clEnqueueReadBuffer(queue, pixelBuffer, CL_TRUE, 0,
                (long) Sizeof.cl_int * size, Pointer.to(pixels),
                0, null, null);
    }

    private void printBuildLog() {
        long[] logSize = new long[1];
        clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, null, logSize);
        byte[] logData = new byte[(int) logSize[0]];
        clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG,
                logSize[0], Pointer.to(logData), null);
        System.err.println("OPENCL BUILD LOG:\n" + new String(logData));
    }

    public void release() {
        if (pixelBuffer != null) clReleaseMemObject(pixelBuffer);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(queue);
        clReleaseContext(context);
    }
}