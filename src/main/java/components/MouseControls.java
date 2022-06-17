package components;

import engine.*;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import renderer.DebugDraw;
import renderer.PickingTexture;
import scenes.Scene;
import util.Settings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    private GameObject holdingObject = null;
    private float debounceTime = 0.05f;
    private float debounce = debounceTime;
    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();


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
            if (go.transform.position.x == this.holdingObject.transform.position.x
                    && go.transform.position.y == this.holdingObject.transform.position.y
                    && go.transform.zIndex == this.holdingObject.transform.zIndex
                    && go.getComponent(NonPickable.class) == null){
                return false;
            }
        }
        GameObject newObj = this.holdingObject.copy();
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,1));
        newObj.removeComponent(NonPickable.class);
        if (newObj.getComponent(StateMachine.class) != null){
            StateMachine stateMachine = newObj.getComponent(StateMachine.class);
            stateMachine.refreshTextures();
        }

        Window.getScene().addGameObjectToScene(newObj);
        return true;
    }

    @Override
    public void editorUpdate(float dt){
        debounce -=dt;

        PickingTexture pickingTexture = Window.getImGuiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.getScene();

        if (holdingObject != null && debounce <=0){
            holdingObject.transform.position.x = MouseListener.getWorldX();
            holdingObject.transform.position.y = MouseListener.getWorldY();
            holdingObject.transform.position.x =
                    ((int)Math.floor(holdingObject.transform.position.x / Settings.gridWidth) * Settings.gridWidth)
                            + Settings.gridWidth / 2.0f;
            holdingObject.transform.position.y =
                    ((int)Math.floor(holdingObject.transform.position.y / Settings.gridHeight) * Settings.gridHeight)
                            + Settings.gridHeight / 2.0f;

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
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0
                && !MouseListener.isDragging()){
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x,y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null){
                Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()){
                Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
                Window.getImGuiLayer().getPropertiesWindow().clearSelected();
            }

            this.debounce = 0.2f;
        } else if (MouseListener.isDragging()
                && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                && !GizmoSystem.isUsingGizmos
                && this.holdingObject == null){

            if (!boxSelectSet){
                Window.getImGuiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize =
                    new Vector2f((boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D(new Vector2f(boxSelectStartWorld).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f), 0);

        } else if (boxSelectSet){
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX){
                int temp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = temp;
            }
            if (screenEndY < screenStartY){
                int temp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = temp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX,screenStartY),
                    new Vector2i(screenEndX,screenEndY));



            Set<Integer> uniqueGameObjectIds = new HashSet<>();

            for (float objId : gameObjectIds){
                uniqueGameObjectIds.add((int)objId);
            }

            for (Integer objId : uniqueGameObjectIds){
                GameObject pickedObj = Window.getScene().getGameObject(objId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null){
                    Window.getImGuiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
                }
            }

            if (Window.getImGuiLayer().getPropertiesWindow().getActiveGameObjects().size() == 1){
                Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(
                        Window.getImGuiLayer().getPropertiesWindow().getActiveGameObjects().get(0));
            }

        }
    }

    public GameObject getHoldingObject() {
        return holdingObject;
    }
}
