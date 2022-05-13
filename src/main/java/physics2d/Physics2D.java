package physics2d;

import engine.GameObject;
import engine.Transform;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.Circle2DCollider;
import physics2d.components.RigidBody2D;

public class Physics2D {

    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);
    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public void add(GameObject go){
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if (rb != null && rb.getRawBody() == null){
            Transform transform = go.transform;

            //Rigid body definition
            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x,transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuousCollision();

            switch (rb.getBodyType()){
                case KINEMATIC: bodyDef.type = BodyType.KINEMATIC; break;
                case STATIC: bodyDef.type = BodyType.STATIC; break;
                case DYNAMIC: bodyDef.type = BodyType.DYNAMIC; break;
            }

            //Determine collision shape
            PolygonShape shape = new PolygonShape();
            Circle2DCollider circleCollider = null;
            Box2DCollider boxCollider = null;

            //If statement structure assigns the object then checks to see if its null
            if ((circleCollider = go.getComponent(Circle2DCollider.class)) != null){
                shape.setRadius(circleCollider.getRadius());
            } else if ((boxCollider = go.getComponent(Box2DCollider.class)) != null){
                //mul by Half is to match with box2d physics engine
                Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = new Vector2f(boxCollider.getOrigin());
                shape.setAsBox(halfSize.x,halfSize.y, new Vec2(origin.x,origin.y),0);

                //Fit physics box over sprite box
                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos,yPos);
            }
            Body body = this.world.createBody(bodyDef);
            rb.setRawBody(body);
            body.createFixture(shape, rb.getMass());
        }
    }

    public void update(float dt){
        physicsTime += dt;
        //Update physics every 60 frames
        if (physicsTime >= 0.0f){
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }

    }
}
