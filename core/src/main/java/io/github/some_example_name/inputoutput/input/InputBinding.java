package io.github.some_example_name.inputoutput.input;

/**
 * Links an input (key, mouse button, etc.) to a game action.
 */
public class InputBinding {

    private final InputDeviceType deviceType;
    private final int code;
    private final InputProvider inputProvider;

    public InputBinding(InputDeviceType deviceType, int code, InputProvider inputProvider) {
        this.deviceType = deviceType;
        this.code = code;
        this.inputProvider = inputProvider;
    }

    public InputDeviceType getDeviceType() {
        return deviceType;
    }

    public int getCode() {
        return code;
    }

    public InputProvider getInputProvider() {
        return inputProvider;
    }

    public boolean isActive() {
        return inputProvider.isActive(code);
    }

    @Override
    public String toString() {
        return "InputBinding{" +
                "deviceType=" + deviceType +
                ", code=" + code +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputBinding that = (InputBinding) o;

        if (code != that.code) return false;
        return deviceType == that.deviceType;
    }

    @Override
    public int hashCode() {
        int result = deviceType.hashCode();
        result = 31 * result + code;
        return result;
    }
}
