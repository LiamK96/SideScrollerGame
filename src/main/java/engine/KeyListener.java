package engine;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;
    private boolean[] keyPressed = new boolean[GLFW_KEY_LAST+2];
    private boolean[] keyBeginPress = new boolean[GLFW_KEY_LAST+2];

    private KeyListener() {

    }

    public static void endFrame() {
        Arrays.fill(get().keyBeginPress, false);
    }

    public static KeyListener get() {
        if (KeyListener.instance==null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_UNKNOWN) {
            return;
        }
        if (action==GLFW_PRESS) {
            get().keyPressed[key]= true;
            get().keyBeginPress[key] = true;
        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key]=false;
            get().keyBeginPress[key] = false;
        }
    }
    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }

    public static boolean keyBeginPress(int keyCode) {
//        boolean result = get().keyBeginPress[keyCode];
//        if (result){
//            get().keyBeginPress[keyCode] = false;
//        }
//        return result;
        return get().keyBeginPress[keyCode];
    }
}
