package tamu.edu.smartcurtain;

public class AlgorithmLogic {
    private int desried_temp, curr_temp, window_temp;
    private boolean shouldOpen = false;

    AlgorithmLogic(int current, int desired, int actual) {
        this.curr_temp= current;
        this.desried_temp = desired;
        this.window_temp = actual;

        System.out.println("Creating algorithm object with (" + current + ", " + desired + ", " + actual + ")");
    }

    public void setDesired_temp(int desired_temp) {
        System.out.println("Changing desired temp to " + desired_temp);
        this.desried_temp = desired_temp;
    }

    public void setCurr_temp(int curr_temp) {
        System.out.println("Changing current temp to " + curr_temp);
        this.curr_temp = curr_temp;
    }

    public void setWindow_temp(int window_temp) {
        System.out.println("Changing window temp to " + window_temp);
        this.window_temp = window_temp;
    }

    public int getWindow_temp() {
        return window_temp;
    }

    public void computeShouldOpen() {
        // TODO: Write entire algorithm
        System.out.println("Desired temp = " + desried_temp + " | " + "current temp = " + curr_temp + " | " + "window temperature = " + window_temp);
        shouldOpen = desried_temp > curr_temp && window_temp > curr_temp;
    }

    public boolean isShouldOpen() {
        return shouldOpen;
    }
}
