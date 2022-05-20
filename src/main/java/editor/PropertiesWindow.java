package editor;

import components.NonPickable;
import engine.GameObject;
import engine.MouseListener;
import imgui.ImGui;
import physics2d.components.Box2DCollider;
import physics2d.components.Circle2DCollider;
import physics2d.components.RigidBody2D;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

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
                activeGameObject = pickedObj;
            } else if (pickedObj == null && !MouseListener.isDragging()){
                activeGameObject = null;
            }

            this.debounceTime = 0.2f;
        }
    }

    public void imgui(){
        if (activeGameObject != null){
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")){
                if (ImGui.menuItem("Add RigidBody")){
                    if (activeGameObject.getComponent(RigidBody2D.class) == null){
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }
                if (ImGui.menuItem("Add Box Collider")){
                    if (activeGameObject.getComponent(Box2DCollider.class) == null
                            && activeGameObject.getComponent(Circle2DCollider.class) == null){
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }
                if (ImGui.menuItem("Add Circle Collider")){
                    if (activeGameObject.getComponent(Circle2DCollider.class) == null
                            && activeGameObject.getComponent(Box2DCollider.class) == null){
                        activeGameObject.addComponent(new Circle2DCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject(){
        return this.activeGameObject;
    }

    public void setActiveGameObject(GameObject go) {
        this.activeGameObject = go;
    }
}