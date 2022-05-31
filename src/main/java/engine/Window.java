package engine;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;
import util.Settings;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean runtimePlaying = false;

    private static Window window = null;

    private static Scene currentScene = null;


    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Definitely not an Italian Plumber";

        EventSystem.addObserver(this);
    }

    public static void changeScene(SceneInitializer sceneInitializer){
        if (currentScene != null){
            currentScene.destroy();
        }
        getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
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

    @Override
    public void onNotify(GameObject go, Event event) {
        switch (event.type){
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case UserEvent:
                break;
        }
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

        //Framebuffer and Picking Texture MUST have the same width and height
        this.framebuffer = new Framebuffer(Window.getWidth(), Window.getHeight());
        this.pickingTexture = new PickingTexture(Window.getWidth(), Window.getHeight());
        glViewport(0,0,Window.getWidth(),Window.getHeight());

        //ImGui creation and init
        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();

        Window.changeScene(new LevelEditorSceneInitializer());
    }

    public void loop(){
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        System.out.println("loading");
        while (!glfwWindowShouldClose(glfwWindow)){
            //Poll events
            glfwPollEvents();

            //Render Pass 1, render to pickingTexture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0,0,Window.getWidth(),Window.getHeight());
            glClearColor(0.0f,0.0f,0.0f,0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);


            //Render Pass 2, render Actual Game
            //drawing setup
            DebugDraw.beginFrame();

            //bind framebuffer
            this.framebuffer.bind();

            glClearColor(1,1,1,1);
            glClear(GL_COLOR_BUFFER_BIT);


            if (dt >= 0.0f) {
                DebugDraw.draw(); //draw lines and other objects
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    currentScene.update(dt);
                } else{
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();
            }
            this.framebuffer.unbind();


            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                glfwSetWindowShouldClose(glfwWindow, true);
            }

            this.imGuiLayer.update(dt, currentScene);

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();

            //time
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;

        }
    }

//    public static int getWidth(){
//        return get().width;
//    }
    public static int getWidth(){
        return Settings.getWindowWidth();
    }

//    public static int getHeight(){
//        return get().height;
//    }
    public static int getHeight(){
        return Settings.getWindowHeight();
    }

    public static void setWidth(int newWidth){
        get().width = newWidth;
    }

    public static void setHeight(int newHeight){
        get().height = newHeight;
    }

    public static Framebuffer getFramebuffer(){
        return get().framebuffer;
    }

    public static ImGuiLayer getImGuiLayer(){
        return get().imGuiLayer;
    }

    public static long getGlfwWindow(){
        return get().glfwWindow;
    }


}
