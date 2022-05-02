package tamu.edu.smartcurtain;

public class AlgorithmLogic {
    private float desried_temp;
    private float curr_temp;
    private float window_temp;
    private boolean shouldOpen = false;

    AlgorithmLogic(float current, float desired, float actual) {
        this.curr_temp= current;
        this.desried_temp = desired;
        this.window_temp = actual;

        System.out.println("Creating algorithm object with (" + current + ", " + desired + ", " + actual + ")");
    }

    public void setDesired_temp(float desired_temp) {
        System.out.println("Changing desired temp to " + desired_temp);
        this.desried_temp = desired_temp;
    }

    public void setCurr_temp(float curr_temp) {
        System.out.println("Changing current temp to " + curr_temp);
        this.curr_temp = curr_temp;
    }

    public void setWindow_temp(float window_temp) {
        System.out.println("Changing window temp to " + window_temp);
        this.window_temp = window_temp;
    }

    public float getWindow_temp() {
        return window_temp;
    }

    public void computeShouldOpen() {
        // TODO: Write entire algorithm
        if (desried_temp > curr_temp && window_temp > curr_temp) {
            shouldOpen = true;
        } else if (curr_temp > desried_temp) {
            shouldOpen = false;
        } else {
            shouldOpen = false;
        }
    }

    public boolean isShouldOpen() {
        return shouldOpen;
    }
}
