package scenes;

import engine.GameObject;

public abstract class SceneInitializer {
    public abstract void init(Scene scene);
    public abstract void loadResources(Scene scene);
    public abstract void imgui();
    public abstract GameObject getSceneComponentObject();


}
