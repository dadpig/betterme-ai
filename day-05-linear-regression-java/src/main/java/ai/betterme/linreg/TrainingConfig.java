package ai.betterme.linreg;

/**
 * Hyperparameters for batch gradient descent.
 *
 * @param learningRate step size α
 * @param epochs       max number of full passes over the dataset
 * @param tolerance    if |loss_prev − loss_curr| drops below this, stop early
 * @param logEvery     print loss every N epochs (0 = silent)
 */
public record TrainingConfig(
        double learningRate,
        int epochs,
        double tolerance,
        int logEvery
) {
    public TrainingConfig {
        if (learningRate <= 0) {
            throw new IllegalArgumentException("learningRate must be > 0");
        }
        if (epochs <= 0) {
            throw new IllegalArgumentException("epochs must be > 0");
        }
        if (tolerance < 0) {
            throw new IllegalArgumentException("tolerance must be >= 0");
        }
    }

    public static TrainingConfig defaults() {
        return new TrainingConfig(0.02, 10_000, 1e-9, 1_000);
    }
}
