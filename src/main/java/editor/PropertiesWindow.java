package editor;

import components.NonPickable;
import engine.GameObject;
import engine.MouseListener;
import engine.Window;
import imgui.ImGui;
import physics2d.components.Box2DCollider;
import physics2d.components.Circle2DCollider;
import physics2d.components.RigidBody2D;
import renderer.PickingTexture;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    private List<GameObject> activeGameObjects = new ArrayList<>();
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounceTime = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene){
        debounceTime -= dt;

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounceTime < 0 && !MouseListener.isDragging()){
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x,y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null){
                setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()){
                setActiveGameObject(null);
            }

            this.debounceTime = 0.2f;
        }
    }

    public void imgui(){
        if (getActiveGameObject() != null){
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")){
                if (ImGui.menuItem("Add RigidBody")){
                    if (getActiveGameObject().getComponent(RigidBody2D.class) == null){
                        getActiveGameObject().addComponent(new RigidBody2D());
                    }
                }
                if (ImGui.menuItem("Add Box Collider")){
                    if (getActiveGameObject().getComponent(Box2DCollider.class) == null
                            && getActiveGameObject().getComponent(Circle2DCollider.class) == null){
                        getActiveGameObject().addComponent(new Box2DCollider());
                    }
                }
                if (ImGui.menuItem("Add Circle Collider")){
                    if (getActiveGameObject().getComponent(Circle2DCollider.class) == null
                            && getActiveGameObject().getComponent(Box2DCollider.class) == null){
                        getActiveGameObject().addComponent(new Circle2DCollider());
                    }
                }

                ImGui.endPopup();
            }

            getActiveGameObject().imgui();
            ImGui.end();
        }
    }

    public void clearSelected(){
        this.activeGameObjects.clear();
    }

    public GameObject getActiveGameObject(){
        return activeGameObjects.isEmpty() ? activeGameObject : null;
    }

    public void setActiveGameObject(GameObject go) {
        if (activeGameObjects.size() > 1){
            clearSelected();
        }
        this.activeGameObject = go;
    }

    public List<GameObject> getActiveGameObjects(){
        return activeGameObjects;
    }

    public void addActiveGameObject(GameObject go){
        if (activeGameObject != null){
            activeGameObject =null;
        }
        this.activeGameObjects.add(go);
    }
}
