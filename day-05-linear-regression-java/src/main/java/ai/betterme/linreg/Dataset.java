package ai.betterme.linreg;

/**
 * Immutable training dataset.
 *
 * @param xs feature matrix of shape [n_samples][n_features]
 * @param ys target vector of length n_samples
 */
public record Dataset(double[][] xs, double[] ys) {

    public Dataset {
        if (xs == null || ys == null) {
            throw new IllegalArgumentException("xs and ys must be non-null");
        }
        if (xs.length == 0) {
            throw new IllegalArgumentException("dataset must be non-empty");
        }
        if (xs.length != ys.length) {
            throw new IllegalArgumentException(
                "row count mismatch: xs=" + xs.length + " ys=" + ys.length);
        }
        int nFeatures = xs[0].length;
        for (int i = 1; i < xs.length; i++) {
            if (xs[i].length != nFeatures) {
                throw new IllegalArgumentException(
                    "ragged feature row at index " + i + ": expected " + nFeatures
                        + " features, got " + xs[i].length);
            }
        }
    }

    public int nSamples() {
        return ys.length;
    }

    public int nFeatures() {
        return xs[0].length;
    }
}
