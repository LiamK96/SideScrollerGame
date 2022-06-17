package components;

import engine.KeyListener;
import engine.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;

public class GizmoSystem extends Component{
    private Spritesheet gizmoSprites;
    private int usingGizmo = 0;

    private float debounceTime = 0.2f;

    protected static boolean isUsingGizmos = false;

    public GizmoSystem(Spritesheet gizmoSprites){
        this.gizmoSprites = gizmoSprites;
    }

    @Override
    public void start(){
        gameObject.addComponent(new TranslateGizmo(gizmoSprites.getSprite(1),
                Window.getImGuiLayer().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(gizmoSprites.getSprite(2),
                Window.getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void editorUpdate(float dt){
        debounceTime -= dt;
        if (usingGizmo == 0){
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).notUsing();
            isUsingGizmos = gameObject.getComponent(TranslateGizmo.class).isHoveringOver();
        } else if (usingGizmo == 1){
            gameObject.getComponent(TranslateGizmo.class).notUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
            isUsingGizmos = gameObject.getComponent(ScaleGizmo.class).isHoveringOver();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_E) && debounceTime <= 0){
            if (usingGizmo == 1){
                usingGizmo = 0;
            } else {
                usingGizmo++;
            }
            debounceTime = 0.2f;
        }

    }

}
