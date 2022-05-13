package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import engine.Camera;
import engine.GameObject;
import engine.GameObjectDeserializer;
import engine.Transform;
import imgui.ImGui;
import org.joml.Vector2f;
import renderer.Renderer;

import java.awt.font.GlyphMetrics;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private Renderer renderer = new Renderer();
    private Camera camera;
    private boolean isRunning = false;
    private List<GameObject> gameObjects = new ArrayList<>();
    private boolean levelLoaded = false;

    private SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer){
        this.sceneInitializer = sceneInitializer;
    }

    public void init(){
        this.camera = new Camera(new Vector2f());
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start(){
        for (int i = 0; i < gameObjects.size(); i++){
            GameObject go = gameObjects.get(i);
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go){
        if (!isRunning){
            gameObjects.add(go);
        }else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public List<GameObject> getGameObjects(){
        return this.gameObjects;
    }

    public GameObject getGameObject(int gameObjectId){
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(GameObject -> GameObject.getUid() == gameObjectId)
                .findFirst();
        return result.orElse(null);
    }

    public void update(float dt){
        this.camera.adjustProjection();

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }
    public void render(){
        this.renderer.render();
    }

    public Camera getCamera(){
        return this.camera;
    }

    public void imgui(){
        this.sceneInitializer.imgui();
    }

    public GameObject createGameobject(String name){
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void saveExit(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt",false);
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj : this.gameObjects){
                if (obj.doSerialization()){
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e){
            System.out.println("inFile(No Space after :):"+inFile);
            e.printStackTrace();

        }

        if (!inFile.equals("")&&!inFile.equals("[]")){
            int maxCompId = -1;
            int maxGoId = -1;
            GameObject[] objs = gson.fromJson(inFile,GameObject[].class);
            for (int i = 0; i<objs.length; i++){
                addGameObjectToScene(objs[i]);

                for (Component c : objs[i].getAllComponents()){
                    if (c.getUid()>maxCompId){
                        maxCompId = c.getUid();
                    }
                }
                if (objs[i].getUid() > maxGoId){
                    maxGoId = objs[i].getUid();
                }
            }
            maxCompId++;
            maxGoId++;
            Component.init(maxCompId);
            GameObject.init(maxGoId);
            this.levelLoaded = true;
        }
    }

}
