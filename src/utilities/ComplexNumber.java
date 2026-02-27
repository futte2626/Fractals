package utilities;

public class ComplexNumber {
    public double real;
    public double imag;

    public ComplexNumber(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(real + other.real, imag+ other.imag);
    }

    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(real- other.real, imag - other.imag);
    }

    public ComplexNumber multiply(ComplexNumber other) {
        return new ComplexNumber(real*other.real-imag*other.imag, imag*other.real+real*other.imag);
    }

    public double getMagnitudeSquared() {
        return real*real + imag*imag;
    }

    public double getMagnitude() {
        return Math.sqrt(real*real + imag*imag);
    }
}
