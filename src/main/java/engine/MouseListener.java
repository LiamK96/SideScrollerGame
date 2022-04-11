package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import scenes.Scene;


import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX;
    private boolean mouseButtonPressed[] = new boolean[GLFW_MOUSE_BUTTON_LAST+1];
    private boolean isDragging;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener get(){
        if (MouseListener.instance == null){
            instance = new MouseListener();
        }
        return instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos){
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2]|| get().mouseButtonPressed[3]
                || get().mouseButtonPressed[4]|| get().mouseButtonPressed[5]|| get().mouseButtonPressed[6]|| get().mouseButtonPressed[7];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods){
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE){
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
        get().lastX = get().xPos;
        get().lastY = get().yPos;

    }

    public static float getX(){
        return (float) get().xPos;
    }

    public static float getY(){
        return (float) get().yPos;
    }

    public static float getDx(){
        return (float) (get().lastX-get().xPos);
    }

    public static float getDy(){
        return (float) (get().lastY-get().yPos);
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

    //TODO remove hardcoded window sizes for getScreenX and getScreenY
    public static float getScreenX(){
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / (float)get().gameViewportSize.x) * 1920.0f;

        return currentX;
    }

    public static float getScreenY(){
        float currentY = getY() - get().gameViewportPos.y;

        currentY = 1080.0f - ((currentY / (float)get().gameViewportSize.y) * 1080.0f);

        return currentY;
    }

    public static float getOrthoX() {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / (float)get().gameViewportSize.x) * 2.0f - 1.0f;
        Vector4f temp = new Vector4f(currentX,0,0,1);

        Camera camera = Window.getScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        temp.mul(viewProjection);

        currentX = temp.x;
        return currentX;
    }

    public static float getOrthoY() {
        float currentY = getY() - get().gameViewportPos.y;

        //ImGUI uses flipped vec.y than openGL, therefor we need to make the result negative thus the minus.
        currentY = -((currentY / (float)get().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f temp = new Vector4f(0,currentY,0,1);

        Camera camera = Window.getScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        temp.mul(viewProjection);

        currentY = temp.y;
        return currentY;
    }


    //TODO find a better way to set mouse position in viewport
    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }
}
