package engine;

import components.RigidBody;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector4f;
import scenes.Scene;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[GLFW_KEY_LAST+2];
    private boolean keyBeginPress[] = new boolean[GLFW_KEY_LAST+2];

    private KeyListener(){

    }

    public static KeyListener get(){
        if (KeyListener.instance==null){
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        if (action==GLFW_PRESS){
            get().keyPressed[key]= true;
            get().keyBeginPress[key] = true;
        } else if (action == GLFW_RELEASE){
            get().keyPressed[key]=false;
            get().keyBeginPress[key] = false;
        }
    }
    public static boolean isKeyPressed(int keyCode){
        return get().keyPressed[keyCode];
    }

    public static boolean keyBeginPress(int keyCode){
        boolean result = get().keyBeginPress[keyCode];
        if (result){
            get().keyBeginPress[keyCode] = false;
        }
        return result;
    }
}
