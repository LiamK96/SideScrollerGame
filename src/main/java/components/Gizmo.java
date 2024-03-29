package components;

import editor.PropertiesWindow;
import engine.*;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component {
    private Vector4f xAxisColor = new Vector4f(1,0.3f,0.3f,1);
    private Vector4f xAxisColorHover = new Vector4f(1,0,0,1);
    private Vector4f yAxisColor = new Vector4f(0.3f,1,0.3f,1);
    private Vector4f yAxisColorHover = new Vector4f(0,1,0,1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    //protected GameObject activeGameObject = Scene.getActiveGameObject();

    private Vector2f xAxisOffset = new Vector2f(15f/80f,-8f/80f);
    private Vector2f yAxisOffset = new Vector2f(-6f/80f, 15f/80f);

    private float gizmoWidth = 15f / 100f;
    private float gizmoHeight = 30f / 100f;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false;
    private boolean isBeingUsed = false;

    protected PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);

        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        this.propertiesWindow = propertiesWindow;

        Window.getScene().addGameObjectToScene(xAxisObject);
        Window.getScene().addGameObjectToScene(yAxisObject);
    }

    @Override
    public void start(){
        this.xAxisObject.transform.rotation = 90.0f;
        this.yAxisObject.transform.rotation = 180.0f;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if (using) {
            this.setInactive();
        }

        xAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
        yAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
    }

    @Override
    public void editorUpdate(float dt) {
        if (!using) {
            return;
        }
        //this.activeGameObject = Scene.getActiveGameObject();
        if (propertiesWindow.getActiveGameObject() != null) {
            setActive();
        } else{
            setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if (((xAxisHot || xAxisActive) && !yAxisActive) && MouseListener.isDragging()
                && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if (((yAxisHot || yAxisActive)&& !xAxisActive) && MouseListener.isDragging()
                && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (propertiesWindow.getActiveGameObject() != null) {
            this.xAxisObject.transform.position.set(propertiesWindow.getActiveGameObject().transform.position);
            this.yAxisObject.transform.position.set(propertiesWindow.getActiveGameObject().transform.position);
            this.xAxisObject.transform.position.add(xAxisOffset);
            this.yAxisObject.transform.position.add(yAxisOffset);
        }
    }

    private void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive() {
        //Scene.setActiveGameObject(null);
        this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.yAxisSprite.setColor(new Vector4f(0,0,0,0));
    }

    public boolean isHoveringOver() {
        return propertiesWindow.getActiveGameObject() != null && isBeingUsed();
    }

    private boolean isBeingUsed() {
        if (isBeingUsed) {
            if (!MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                isBeingUsed = false;
            }
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                && this.using
                && (checkXHoverState() || checkYHoverState())) {
            this.isBeingUsed = true;
        }
        return isBeingUsed;
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight/2.0f)
                && mousePos.x >= xAxisObject.transform.position.x - (gizmoHeight/2.0f)
                && mousePos.y >= xAxisObject.transform.position.y - (gizmoWidth/2.0f)
                && mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth/2.0f)) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }
        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState(){
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth/2.0f)
                && mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth/2.0f)
                && mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight/2.0f)
                && mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight/2.0f)) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }
        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing() {
        this.using = true;
    }

    public void notUsing() {
        this.using = false;
        this.setInactive();
    }

    public boolean isUsing() {
        return this.using;
    }
}
