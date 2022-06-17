package physics2d.components;

import components.Component;
import components.RigidBody;
import engine.Window;
import org.joml.Vector2f;

public class PillboxCollider extends Component {

    private transient Circle2DCollider topCircle = new Circle2DCollider();
    private transient Circle2DCollider bottomCircle = new Circle2DCollider();
    private transient Box2DCollider box = new Box2DCollider();
    private transient boolean resetFixtureNextFrame = false;

    public float width = 0.1f;
    public float height = 0.2f;
    public Vector2f offset = new Vector2f();


    @Override
    public void start(){
        this.topCircle.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.box.gameObject = this.gameObject;

        recalculateColliders();
    }

    @Override
    public void update(float dt){
        if (resetFixtureNextFrame){
            resetFixture();
        }
    }

    @Override
    public void editorUpdate(float dt){
        topCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);
        box.editorUpdate(dt);

        if (resetFixtureNextFrame){
            resetFixture();
        }
    }

    public void resetFixture(){
        if (Window.getPhysics().isLocked()){
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if (gameObject != null){
            RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
            if (rb != null){
                Window.getPhysics().resetPillboxCollider(rb, this);
            }
        }
    }

    public void recalculateColliders(){
        float circleRadius = width/4;
        float boxHeight = height - 2 * circleRadius;
        topCircle.setRadius(circleRadius);
        bottomCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(offset).add(0, boxHeight / 4.0f));
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));
        box.setHalfSize(new Vector2f(width/2.0f, boxHeight/2.0f));
        box.setOffset(offset);
    }

    public Circle2DCollider getTopCircle() {
        return topCircle;
    }

    public Circle2DCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBox() {
        return box;
    }

    public void setWidth(float newWidth){
        this.width = newWidth;
        recalculateColliders();
        resetFixture();
    }
    public void setHeight(float newHeight){
        this.height = newHeight;
        recalculateColliders();
        resetFixture();
    }
}
