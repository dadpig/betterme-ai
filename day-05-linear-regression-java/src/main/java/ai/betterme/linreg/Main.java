package ai.betterme.linreg;

import java.util.Random;

/**
 * Day 5 driver — run with: java -cp out ai.betterme.linreg.Main
 *
 * Generates synthetic y = 3x + 7 + ε, trains, then asserts recovered parameters.
 */
public final class Main {

    public static void main(String[] args) {
        var data = syntheticUnivariate(/*n=*/ 500, /*w=*/ 3.0, /*b=*/ 7.0, /*noise=*/ 0.1, /*seed=*/ 42);
        System.out.println("Dataset shape: " + data.nSamples() + " samples × " + data.nFeatures() + " features");

        var model = new LinearRegression().train(data, TrainingConfig.defaults());
        System.out.printf("Recovered: w=%.4f b=%.4f%n", model.weights()[0], model.bias());

        assert Math.abs(model.weights()[0] - 3.0) < 0.1 : "w not recovered";
        assert Math.abs(model.bias() - 7.0) < 0.1       : "b not recovered";
    }

    static Dataset syntheticUnivariate(int n, double w, double b, double noise, long seed) {
        var rng = new Random(seed);
        var xs = new double[n][1];
        var ys = new double[n];
        for (int i = 0; i < n; i++) {
            double x = rng.nextDouble() * 10.0;
            xs[i][0] = x;
            ys[i] = w * x + b + rng.nextGaussian() * noise;
        }
        return new Dataset(xs, ys);
    }

    // TODO (stretch): syntheticMultivariate(int n, double[] w, double b, double noise, long seed)
}
