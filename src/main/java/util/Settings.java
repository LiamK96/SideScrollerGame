package util;

import engine.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class Settings {

    public static float gridWidth = 0.25f;
    public static float gridHeight = 0.25f;
    public static GLFWVidMode vidMode = null;

    public static int[] getWindowSizes() {
        int[] windowSize = new int[2];
        int[] windowWidth = new int[1];
        int[] windowHeight = new int[1];
        GLFW.glfwGetWindowSize(Window.getGlfwWindow(), windowWidth, windowHeight);

        windowSize[0] = windowWidth[0];
        windowSize[1] = windowHeight[0];

        return windowSize;
    }

    public static int getWindowWidth() {
        return getWindowSizes()[0];
    }

    public static int getWindowHeight() {
        return getWindowSizes()[1];
    }

    public static GLFWVidMode getVidMode() {
        if (vidMode == null) {
            System.out.println(GLFW.glfwGetMonitors());
            vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        }
        return vidMode;
    }

    public static int[] getMonitorSizes() {
        return new int[] {getVidMode().width(), getVidMode().height()};
    }

    public static int getMonitorWidth() {
        return getMonitorSizes()[0];
    }

    public static int getMonitorHeight() {
        return getMonitorSizes()[1];
    }
}
