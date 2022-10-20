package components;

import engine.Camera;
import engine.KeyListener;
import engine.MouseListener;
import engine.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component{

    private float dragDebounce = 0.032f;

    private boolean reset = false;

    private float lerpTime = 0.0f;
    private float dragSensitivity = 20.0f;
    private float scrollSensitivity = 0.1f;

    private Vector2f resetCoords = new Vector2f();

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;

    public EditorCamera(Camera levelEditorCamera){
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt){
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                && KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && dragDebounce > 0){
            this.clickOrigin = MouseListener.getWorld();
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                && KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)){
            Vector2f mousePos = MouseListener.getWorld();
            Vector2f delta = new Vector2f(mousePos).sub(clickOrigin).mul(10.0f);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos,dt);
        }

        if (dragDebounce < 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
            dragDebounce = 0.032f;
        }

        if (MouseListener.getScrollY() != 0.0f){
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity)
                    , 1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            if (levelEditorCamera.getZoom() + addValue > 10.0f){
                levelEditorCamera.setZoom(10.0f);
            } else if (levelEditorCamera.getZoom() + addValue < 0.25f){
                levelEditorCamera.setZoom(0.25f);
            } else {
                levelEditorCamera.addZoom(addValue);
            }
        }

        if (!reset){
            if (KeyListener.isKeyPressed(GLFW_KEY_TAB) && KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
                if (Window.getScene().getGameObject("player") != null){
                    resetCoords.set(Window.getScene().getGameObject("player").transform.position);
                    resetCoords.add(-0.125f,-0.125f);
                    resetCoords.add(-0.25f*12,-0.25f*6);
                    //resetCoords.mul(6,12);
                    System.out.println(resetCoords);

                    reset = true;
                }
            } else if (KeyListener.isKeyPressed(GLFW_KEY_TAB)){
                resetCoords.zero();
                reset = true;
            }
        }

        if (reset){
            levelEditorCamera.position.lerp(new Vector2f(),lerpTime);
            this.lerpTime += dt;
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            if (Math.abs(levelEditorCamera.position.x) <= resetCoords.x + 5.0f &&
                    Math.abs(levelEditorCamera.position.y) <= resetCoords.y + 5.0f){
                levelEditorCamera.position.set(resetCoords);
                this.levelEditorCamera.setZoom(1.0f);
                this.lerpTime = 0f;
                reset = false;
            }
        }
    }

}
