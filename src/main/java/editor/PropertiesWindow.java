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

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    //private GameObject activeGameObject = Scene.getActiveGameObject();
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
                Scene.setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()){
                Scene.setActiveGameObject(null);
            }

            this.debounceTime = 0.2f;
        }
    }

    public void imgui(){
        if (Scene.getActiveGameObject() != null){
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")){
                if (ImGui.menuItem("Add RigidBody")){
                    if (Scene.getActiveGameObject().getComponent(RigidBody2D.class) == null){
                        Scene.getActiveGameObject().addComponent(new RigidBody2D());
                    }
                }
                if (ImGui.menuItem("Add Box Collider")){
                    if (Scene.getActiveGameObject().getComponent(Box2DCollider.class) == null
                            && Scene.getActiveGameObject().getComponent(Circle2DCollider.class) == null){
                        Scene.getActiveGameObject().addComponent(new Box2DCollider());
                    }
                }
                if (ImGui.menuItem("Add Circle Collider")){
                    if (Scene.getActiveGameObject().getComponent(Circle2DCollider.class) == null
                            && Scene.getActiveGameObject().getComponent(Box2DCollider.class) == null){
                        Scene.getActiveGameObject().addComponent(new Circle2DCollider());
                    }
                }

                ImGui.endPopup();
            }

            Scene.getActiveGameObject().imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject(){
        return Scene.getActiveGameObject();
    }

//    public void setActiveGameObject(GameObject go) {
//        this.activeGameObject = Scene.setActiveGameObject(go);
//    }
}
