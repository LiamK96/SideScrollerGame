package editor;

import components.NonPickable;
import components.SpriteRenderer;
import engine.GameObject;
import engine.MouseListener;
import engine.Window;
import imgui.ImGui;
import org.joml.Vector4f;
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
    private List<Vector4f> activeGameObjectsOgColor = new ArrayList<>();
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounceTime = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
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
        if (activeGameObjectsOgColor.size()>0){
            int i = 0;
            for (GameObject go : activeGameObjects){
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null){
                    spr.setColor(this.activeGameObjectsOgColor.get(i));
                }
                i++;
            }
            activeGameObjectsOgColor.clear();
        }
        this.activeGameObjects.clear();
        this.activeGameObject = null;
    }

    public GameObject getActiveGameObject(){
        return activeGameObjects.isEmpty() ? activeGameObject : null;
    }

    public void setActiveGameObject(GameObject go) {
        if (activeGameObjects.size() > 0){
            clearSelected();
        }
        this.activeGameObject = go;
    }

    public List<GameObject> getActiveGameObjects(){
        return activeGameObjects;
    }

    public void addActiveGameObject(GameObject go){
        this.activeGameObject = null;
        SpriteRenderer spriteRenderer = go.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null){
            this.activeGameObjectsOgColor.add(new Vector4f(spriteRenderer.getColor()));
            spriteRenderer.setColor(new Vector4f(0.8f,0.8f,0.0f,0.8f));
        } else {
            this.activeGameObjectsOgColor.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }
}
