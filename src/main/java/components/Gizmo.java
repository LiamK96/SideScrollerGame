package components;

import editor.PropertiesWindow;
import engine.GameObject;
import engine.MouseListener;
import engine.Prefabs;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component{
    private Vector4f xAxisColor = new Vector4f(1,0.3f,0.3f,1);
    private Vector4f xAxisColorHover = new Vector4f(1,0,0,1);
    private Vector4f yAxisColor = new Vector4f(0.3f,1,0.3f,1);
    private Vector4f yAxisColorHover = new Vector4f(0,1,0,1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    protected GameObject activeGameObject = null;

    private Vector2f xAxisOffset = new Vector2f(15f/80f,-8f/80f);
    private Vector2f yAxisOffset = new Vector2f(-6f/80f, 15f/80f);

    private float gizmoWidth = 15f / 80f;
    private float gizmoHeight = 30f / 80f;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false;

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
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
    public void update(float dt){
        if (using){
            this.setInactive();
        }
    }

    @Override
    public void editorUpdate(float dt){
        if (!using){
            return;
        }

        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObject != null){
            setActive();
        } else{
            setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if (((xAxisHot || xAxisActive) && !yAxisActive) && MouseListener.isDragging()
                && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = true;
            yAxisActive = false;
        } else if (((yAxisHot || yAxisActive)&& !xAxisActive) && MouseListener.isDragging()
                && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (this.activeGameObject != null){
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(xAxisOffset);
            this.yAxisObject.transform.position.add(yAxisOffset);
        }
    }

    private void setActive(){
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive(){
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.yAxisSprite.setColor(new Vector4f(0,0,0,0));
    }

    private boolean checkXHoverState(){
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight/2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoHeight/2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoWidth/2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth/2.0f)){
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }
        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState(){
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth/2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth/2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight/2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight/2.0f)){
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }
        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing(){
        this.using = true;
    }

    public void notUsing(){
        this.using = false;
        this.setInactive();
    }
}
