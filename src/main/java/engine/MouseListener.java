package engine;

import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import scenes.Scene;
import util.Settings;


import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, worldX, worldY;
    private boolean mouseButtonPressed[] = new boolean[GLFW_MOUSE_BUTTON_LAST+1];
    private boolean isDragging;

    private int mouseButtonsDown = 0;

    public Vector2f gameViewportPos = new Vector2f();
    public Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
    }

    public static MouseListener get(){
        if (MouseListener.instance == null){
            instance = new MouseListener();
        }
        return instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos){
        if (get().mouseButtonsDown > 0){
            get().isDragging = true;
        }

        get().xPos = xpos;
        get().yPos = ypos;

        //Todo find a fix for this bug
        if (ImGuiLayer.isViewportsEnabled){
            get().yPos+=30.0f;
        }
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods){
        if (action == GLFW_PRESS) {
            get().mouseButtonsDown++;
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE){
            get().mouseButtonsDown--;
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset){
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame(){
        get().scrollX = 0.0;
        get().scrollY = 0.0;

    }

    public static float getX(){
        return (float) get().xPos;
    }

    public static float getY(){
        return (float) get().yPos;
    }

    public static float getScrollX(){
        return (float) get().scrollX;
    }

    public static float getScrollY(){
        return (float) get().scrollY;
    }

    public static boolean isDragging(){
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button){
        if (button < get().mouseButtonPressed.length){
            return get().mouseButtonPressed[button];
        }
        return false;
    }


    public static float getScreenX(){
        return getScreen().x;
    }

    public static float getScreenY(){
        return getScreen().y;
    }
    //TODO lookup how glfwgetWindowSize works
    public static Vector2f getScreen(){
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * (float)Settings.getWindowWidth();
        float currentY = getY() - get().gameViewportPos.y;
        currentY = ((float)Settings.getWindowHeight()) - ((currentY / get().gameViewportSize.y) * ((float)Settings.getWindowHeight()));

        return new Vector2f(currentX,currentY);
    }

    public static float getWorldX(){
        return getWorld().x;
    }


    public static float getWorldY(){
        return getWorld().y;
    }

    public static Vector2f getWorld(){
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        float currentY = (getY() - get().gameViewportPos.y);
        //ImGUI uses flipped vec.y than openGL, therefore we need to make the result negative thus the minus.
        currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f temp = new Vector4f(currentX,currentY,0,1);

        Camera camera = Window.getScene().getCamera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        temp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(temp.x,temp.y);
    }


    //TODO find a better way to set mouse position in viewport
    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }
}
