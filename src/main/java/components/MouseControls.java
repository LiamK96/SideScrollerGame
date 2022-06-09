package components;

import engine.GameObject;
import engine.KeyListener;
import engine.MouseListener;
import engine.Window;
import org.joml.Vector4f;
import scenes.Scene;
import util.Settings;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    private GameObject holdingObject = null;
    private float debounceTime = 0.05f;
    private float debounce = debounceTime;

    public void pickupObject(GameObject go){
        if (this.holdingObject != null){
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f,0.8f,0.8f,0.5f));
        this.holdingObject.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(go);
    }

    public boolean place(){
        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        for (GameObject go : gameObjects){
            if (go.equals(this.holdingObject)){
                continue;
            }
            if (go.transform.position.x == this.holdingObject.transform.position.x &&
                go.transform.position.y == this.holdingObject.transform.position.y &&
                go.transform.zIndex == this.holdingObject.transform.zIndex){
                return false;
            }
        }
        GameObject newObj = this.holdingObject.copy();
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,1));
        newObj.removeComponent(NonPickable.class);
        if (newObj.getComponent(StateMachine.class) != null){
            StateMachine stateMachine = newObj.getComponent(StateMachine.class);
            stateMachine.refeshTextures();
        }

        Window.getScene().addGameObjectToScene(newObj);
        return true;
    }

    @Override
    public void editorUpdate(float dt){
        debounce -=dt;
        if (holdingObject != null && debounce <=0){
            holdingObject.transform.position.x = MouseListener.getWorldX();
            holdingObject.transform.position.y = MouseListener.getWorldY();
            holdingObject.transform.position.x = ((int)Math.floor(holdingObject.transform.position.x / Settings.gridWidth) * Settings.gridWidth)+ Settings.gridWidth / 2.0f;
            holdingObject.transform.position.y = ((int)Math.floor(holdingObject.transform.position.y / Settings.gridHeight) * Settings.gridHeight) + Settings.gridHeight / 2.0f;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                if(!place()){
                    Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
                }
                debounce = debounceTime;
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_BACKSPACE)){
                holdingObject.destroy();
                holdingObject = null;
            }
        }
    }

    public GameObject getHoldingObject() {
        return holdingObject;
    }
}
