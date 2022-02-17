package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import renderer.DebugDraw;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;

    public float r,g,b,a;

    private static Window window = null;

    private static Scene currentScene = null;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        r=1;
        g=1;
        b=1;
        a=1;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown Scene: "+ newScene;
                break;
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    public void run(){
        System.out.println("LWJGL version: "+ Version.getVersion());
        init();
        loop();

        //free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //terminate imgui
        imGuiLayer.destroyImGui();

        //terminate glfw
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init(){
        //Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialise GLFW");
        }

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED,GLFW_TRUE); //Error if placed here


        //create window;
        glfwWindow = glfwCreateWindow(this.width,this.height,this.title, NULL,NULL);
        if (glfwWindow==NULL){
            throw new IllegalStateException("Failed to create window");
        }



        glfwSetCursorPosCallback(glfwWindow,MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });


        //make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        //Enable v-sync;
        glfwSwapInterval(1);

        glfwMaximizeWindow(glfwWindow); //Maximise window

        //make window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        //Enable Alpha blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE,GL_ONE_MINUS_SRC_ALPHA);

        //ImGui creation and init
        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();

        Window.changeScene(0);
    }

    public void loop(){
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;


        System.out.println("loading");
        while (!glfwWindowShouldClose(glfwWindow)){
            //Poll events
            glfwPollEvents();

            //drawing setup
            DebugDraw.beginFrame();

            glClearColor(r,g,b,a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0.0f) {
                DebugDraw.draw(); //draw lines and other objects
                currentScene.update(dt);
            }


            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                glfwSetWindowShouldClose(glfwWindow, true);
            }

            this.imGuiLayer.update(dt, currentScene);

            glfwSwapBuffers(glfwWindow);

            //time
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;

        }
        currentScene.saveExit();
    }

    public static int getWidth(){
        return get().width;
    }

    public static int getHeight(){
        return get().height;
    }

    public static void setWidth(int newWidth){
        get().width = newWidth;
    }

    public static void setHeight(int newHeight){
        get().height = newHeight;
    }

}
