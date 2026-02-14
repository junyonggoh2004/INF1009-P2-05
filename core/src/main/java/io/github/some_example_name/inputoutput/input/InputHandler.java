package io.github.some_example_name.inputoutput.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central input handler using an action + binding system.
 */
public class InputHandler implements InputProcessor, Disposable {

    private Map<String, Action> actions;
    private Map<String, List<InputBinding>> actionBindings;

    private InputProvider keyboardProvider;
    private InputProvider mouseProvider;

    private int mouseX;
    private int mouseY;
    private int mouseDeltaX;
    private int mouseDeltaY;

    private int scrollAmount;

    public InputHandler() {
        actions = new HashMap<>();
        actionBindings = new HashMap<>();

        initializeInputProviders();
        setupDefaultBindings();
    }

    public void initializeInputProcessor() {
        if (Gdx.input != null) {
            Gdx.input.setInputProcessor(this);
        }
    }

    private void initializeInputProviders() {
        keyboardProvider = code ->
                Gdx.input != null && Gdx.input.isKeyPressed(code);

        mouseProvider = code ->
                Gdx.input != null && Gdx.input.isButtonPressed(code);
    }

    private void setupDefaultBindings() {
        // Movement
        bindKey(Input.Keys.W, "MOVE_UP");
        bindKey(Input.Keys.S, "MOVE_DOWN");
        bindKey(Input.Keys.A, "MOVE_LEFT");
        bindKey(Input.Keys.D, "MOVE_RIGHT");

        bindKey(Input.Keys.UP, "MOVE_UP");
        bindKey(Input.Keys.DOWN, "MOVE_DOWN");
        bindKey(Input.Keys.LEFT, "MOVE_LEFT");
        bindKey(Input.Keys.RIGHT, "MOVE_RIGHT");

        // Common actions
        bindKey(Input.Keys.SPACE, "ACTION");
        bindKey(Input.Keys.ENTER, "CONFIRM");
        bindKey(Input.Keys.ESCAPE, "CANCEL");
        bindKey(Input.Keys.SHIFT_LEFT, "SPRINT");
        bindKey(Input.Keys.SHIFT_RIGHT, "SPRINT");

        // Mouse
        bindMouseButton(Input.Buttons.LEFT, "PRIMARY");
        bindMouseButton(Input.Buttons.RIGHT, "SECONDARY");
        bindMouseButton(Input.Buttons.MIDDLE, "MIDDLE");
    }

    public void registerAction(String actionName) {
        if (!actions.containsKey(actionName)) {
            actions.put(actionName, new Action(actionName));
            actionBindings.put(actionName, new ArrayList<>());
        }
    }

    public void bindKey(int keyCode, String actionName) {
        registerAction(actionName);
        InputBinding binding =
                new InputBinding(InputDeviceType.KEYBOARD, keyCode, keyboardProvider);
        actionBindings.get(actionName).add(binding);
    }

    public void bindMouseButton(int button, String actionName) {
        registerAction(actionName);
        InputBinding binding =
                new InputBinding(InputDeviceType.MOUSE, button, mouseProvider);
        actionBindings.get(actionName).add(binding);
    }

    public void unbindKey(int keyCode, String actionName) {
        List<InputBinding> bindings = actionBindings.get(actionName);
        if (bindings != null) {
            bindings.removeIf(binding ->
                    binding.getDeviceType() == InputDeviceType.KEYBOARD &&
                    binding.getCode() == keyCode
            );
        }
    }

    public void unbindMouseButton(int button, String actionName) {
        List<InputBinding> bindings = actionBindings.get(actionName);
        if (bindings != null) {
            bindings.removeIf(binding ->
                    binding.getDeviceType() == InputDeviceType.MOUSE &&
                    binding.getCode() == button
            );
        }
    }

    public void clearBindings(String actionName) {
        List<InputBinding> bindings = actionBindings.get(actionName);
        if (bindings != null) {
            bindings.clear();
        }
    }

    public Action getAction(String actionName) {
        return actions.get(actionName);
    }

    public List<InputBinding> getBindings(String actionName) {
        return actionBindings.get(actionName);
    }

    public boolean isActionActive(String actionName) {
        Action action = actions.get(actionName);
        return action != null && action.isActive();
    }

    public boolean isActionPressed(String actionName) {
        Action action = actions.get(actionName);
        return action != null && action.isPressed();
    }

    public boolean isActionHeld(String actionName) {
        Action action = actions.get(actionName);
        return action != null && action.isHeld();
    }

    public boolean isActionReleased(String actionName) {
        Action action = actions.get(actionName);
        return action != null && action.isReleased();
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getMouseDeltaX() {
        return mouseDeltaX;
    }

    public int getMouseDeltaY() {
        return mouseDeltaY;
    }

    public int getScrollAmount() {
        return scrollAmount;
    }

    /**
     * Call once per frame.
     */
    public void update(float dt) {
        mouseDeltaX = 0;
        mouseDeltaY = 0;
        scrollAmount = 0;

        for (Map.Entry<String, List<InputBinding>> entry : actionBindings.entrySet()) {
            String actionName = entry.getKey();
            List<InputBinding> bindings = entry.getValue();
            Action action = actions.get(actionName);

            if (action != null) {
                boolean isPressed = false;

                for (InputBinding binding : bindings) {
                    if (binding.isActive()) {
                        isPressed = true;
                        break;
                    }
                }

                action.update(isPressed);
            }
        }

        if (Gdx.input != null) {
            int newMouseX = Gdx.input.getX();
            int newMouseY = Gdx.input.getY();

            mouseDeltaX = newMouseX - mouseX;
            mouseDeltaY = newMouseY - mouseY;

            mouseX = newMouseX;
            mouseY = newMouseY;
        }
    }

    public void reset() {
        for (Action action : actions.values()) {
            action.reset();
        }

        mouseDeltaX = 0;
        mouseDeltaY = 0;
        scrollAmount = 0;
    }

    public Map<String, Action> getAllActions() {
        return new HashMap<>(actions);
    }

    public Map<String, List<InputBinding>> getAllBindings() {
        return new HashMap<>(actionBindings);
    }

    public InputProvider getKeyboardProvider() {
        return keyboardProvider;
    }

    public InputProvider getMouseProvider() {
        return mouseProvider;
    }

    // InputProcessor implementation

    @Override
    public boolean keyDown(int keycode) { return false; }

    @Override
    public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scrollAmount += (int) amountY;
        return false;
    }

    @Override
    public void dispose() {
        reset();
        actions.clear();
        actionBindings.clear();
    }
}
