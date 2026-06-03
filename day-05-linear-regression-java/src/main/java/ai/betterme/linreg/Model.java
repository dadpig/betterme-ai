package ai.betterme.linreg;

/**
 * Immutable trained linear-regression model.
 *
 * @param weights coefficient per feature (length = n_features)
 * @param bias    scalar intercept term
 */
public record Model(double[] weights, double bias) {

    public Model {
        if (weights == null) {
            throw new IllegalArgumentException("weights must be non-null");
        }
    }

    // TODO (you): implement predict(double[] x) here, OR keep prediction
    // in LinearRegression.predict(Model, double[]) — your call. Defend it
    // in the reflection at the end of the session.
}
