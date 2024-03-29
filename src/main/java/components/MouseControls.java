package components;

import editor.PropertiesWindow;
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
    private boolean isPickup = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    private PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();


    public void pickupObject(GameObject go) {
        if (this.holdingObject != null){
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        this.holdingObject.addComponent(new NonPickable());
        if (this.holdingObject.name.equals("Sprite_Object_Gen")) {
            this.holdingObject.name = "Holding Object";
        }
        Window.getScene().addGameObjectToScene(go);
    }

    public boolean place(){
        //Check to see if space is vacant
        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        for (GameObject go : gameObjects) {
            if (go.equals(this.holdingObject)) {
                continue;
            }
            if (go.transform.position.x == this.holdingObject.transform.position.x
                    && go.transform.position.y == this.holdingObject.transform.position.y
                    && go.transform.zIndex == this.holdingObject.transform.zIndex
                    && go.getComponent(NonPickable.class) == null) {
                return false;
            }
        }
        GameObject newObj = this.holdingObject.copy();
        newObj.name = this.holdingObject.name;
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,1));
        newObj.removeComponent(NonPickable.class);
        if (newObj.name.equals("Holding Object")) {
            newObj.name = "Block: " + newObj.getUid();
        }
        if (newObj.getComponent(StateMachine.class) != null) {
            StateMachine stateMachine = newObj.getComponent(StateMachine.class);
            stateMachine.refreshTextures();
        }

        Window.getScene().addGameObjectToScene(newObj);
        return true;
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -=dt;

        PickingTexture pickingTexture = propertiesWindow.getPickingTexture();
        Scene currentScene = Window.getScene();

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                && KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            //Let the EditorCamera Handle it
        }else if (holdingObject != null && debounce <=0) {
            holdingObject.transform.position.x = MouseListener.getWorldX();
            holdingObject.transform.position.y = MouseListener.getWorldY();
            holdingObject.transform.position.x =
                    ((int)Math.floor(holdingObject.transform.position.x / Settings.gridWidth) * Settings.gridWidth)
                            + Settings.gridWidth / 2.0f;
            holdingObject.transform.position.y =
                    ((int)Math.floor(holdingObject.transform.position.y / Settings.gridHeight) * Settings.gridHeight)
                            + Settings.gridHeight / 2.0f;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                if(!place()) {
                    propertiesWindow.resetActiveGameObject();
                }
                if (isPickup) {
                    destroyHoldingObject();
                }
                debounce = debounceTime;
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_BACKSPACE)) {
                destroyHoldingObject();
            }
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0
                && !MouseListener.isDragging()) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x,y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                //Check to see if we clicked the same obj twice
                if (pickedObj == propertiesWindow.getActiveGameObject()) {
                    isPickup = true;
                    GameObject newObj = pickedObj.copy();
                    newObj.name = pickedObj.name;
                    pickupObject(newObj);
                    pickedObj.destroy();
                    propertiesWindow.resetActiveGameObject();
                } else {
                    propertiesWindow.setActiveGameObject(pickedObj);
                }
            } else if (pickedObj == null && !MouseListener.isDragging()) {
                propertiesWindow.resetActiveGameObject();
            }

            this.debounce = 0.2f;
        } else if (MouseListener.isDragging()
                && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                && !GizmoSystem.isUsingGizmos
                && this.holdingObject == null) {

            if (!boxSelectSet) {
                propertiesWindow.resetActiveGameObject();
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

        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int temp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = temp;
            }
            if (screenEndY < screenStartY) {
                int temp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = temp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX,screenStartY),
                    new Vector2i(screenEndX,screenEndY));



            Set<Integer> uniqueGameObjectIds = new HashSet<>();

            for (float objId : gameObjectIds) {
                uniqueGameObjectIds.add((int)objId);
            }

            propertiesWindow.resetActiveGameObject();

            for (Integer objId : uniqueGameObjectIds) {
                GameObject pickedObj = Window.getScene().getGameObject(objId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                    propertiesWindow.addActiveGameObject(pickedObj);
                }
            }

            if (propertiesWindow.getActiveGameObjects().size() == 1){
                propertiesWindow.setActiveGameObject(
                        propertiesWindow.getActiveGameObjects().get(0));
            }
        }
    }

    public GameObject getHoldingObject() {
        return holdingObject;
    }

    public void destroyHoldingObject() {
        if (holdingObject != null) {
            holdingObject.destroy();
            holdingObject = null;
            isPickup = false;
        }
    }
}
