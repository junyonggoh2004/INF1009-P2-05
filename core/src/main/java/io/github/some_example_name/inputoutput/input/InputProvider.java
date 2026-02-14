package io.github.some_example_name.inputoutput.input;

/**
 * Abstraction for checking if an input code is active.
 */
public interface InputProvider {

    boolean isActive(int code);
}
