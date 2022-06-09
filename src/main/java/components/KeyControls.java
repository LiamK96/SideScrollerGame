package components;

import editor.PropertiesWindow;
import engine.GameObject;
import engine.KeyListener;
import engine.Window;
import org.joml.Vector2f;
import scenes.Scene;
import util.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component{

    private PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();

    @Override
    public void editorUpdate(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)
                && KeyListener.keyBeginPress(GLFW_KEY_D) && propertiesWindow.getActiveGameObject() != null) {
            GameObject newObject = propertiesWindow.getActiveGameObject().copy();
            Window.getScene().addGameObjectToScene(newObject);
            newObject.transform.position.add(new Vector2f(Settings.gridWidth, 0.0f));
            propertiesWindow.setActiveGameObject(newObject);
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                    KeyListener.isKeyPressed(GLFW_KEY_D) && !propertiesWindow.getActiveGameObjects().isEmpty()){
            List<GameObject> gameObjects = new ArrayList<>(propertiesWindow.getActiveGameObjects());
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects){
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
            }
        } else if(KeyListener.keyBeginPress(GLFW_KEY_DELETE)){
            for (GameObject go : propertiesWindow.getActiveGameObjects()){
                go.destroy();
            }
            propertiesWindow.clearSelected();
            if (propertiesWindow.getActiveGameObject() != null){
                propertiesWindow.getActiveGameObject().destroy();
                propertiesWindow.setActiveGameObject(null);
            }
        }
    }

}
