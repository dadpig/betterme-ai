package ai.betterme.linreg;

import java.util.stream.IntStream;

public final class LinearRegression {

    public Model train(Dataset data, TrainingConfig cfg) {
        int n = data.nSamples();
        int d = data.nFeatures();
        double[][] xs = data.xs();
        double[] ys = data.ys();

        double[] weights = new double[d];
        double bias = 0.0;
        Model model = new Model(weights, bias);

        double prevLoss = Double.POSITIVE_INFINITY;

        for (int epoch = 1; epoch <= cfg.epochs(); epoch++) {
            double[] gradW = new double[d];
            double gradB = 0.0;

            for (int i = 0; i < n; i++) {
                double residual = predict(model, xs[i]) - ys[i];
                gradB += residual;
                for (int j = 0; j < d; j++) {
                    gradW[j] += residual * xs[i][j];
                }
            }

            double invN = 1.0 / n;
            for (int j = 0; j < d; j++) {
                weights[j] -= cfg.learningRate() * gradW[j] * invN;
            }
            bias -= cfg.learningRate() * gradB * invN;
            model = new Model(weights, bias);

            double loss = mseLoss(model, data);
            if (Double.isNaN(loss) || Double.isInfinite(loss)) {
                throw new ArithmeticException(
                    "loss diverged to " + loss + " at epoch " + epoch
                        + " — try a smaller learningRate");
            }
            if (cfg.logEvery() > 0 && (epoch == 1 || epoch % cfg.logEvery() == 0)) {
                System.out.printf("epoch %d  loss=%.6f%n", epoch, loss);
            }
            if (Math.abs(prevLoss - loss) < cfg.tolerance()) {
                if (cfg.logEvery() > 0) {
                    System.out.printf("converged at epoch %d  loss=%.6f%n", epoch, loss);
                }
                break;
            }
            prevLoss = loss;
        }

        return model;
    }

    public static double predict(Model model, double[] x) {
        double[] w = model.weights();
        if (x.length != w.length) {
            throw new IllegalArgumentException(
                "feature length " + x.length + " != weights length " + w.length);
        }
        double acc = model.bias();
        for (int j = 0; j < w.length; j++) {
            acc += w[j] * x[j];
        }
        return acc;
    }

    private static double mseLoss(Model model, Dataset data) {
        double[][] xs = data.xs();
        double[] ys = data.ys();
        int n = data.nSamples();
        double sumSq = IntStream.range(0, n)
            .mapToDouble(i -> {
                double r = predict(model, xs[i]) - ys[i];
                return r * r;
            })
            .sum();
        return sumSq / (2.0 * n);
    }
}
