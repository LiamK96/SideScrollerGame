package physics2d;

import engine.GameObject;
import engine.Transform;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
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

    public Physics2D(){
        world.setContactListener(new EngineContactListener());
    }

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
            bodyDef.userData = rb.gameObject; //all userdata will always be gameObject
            bodyDef.bullet = rb.isContinuousCollision();

            switch (rb.getBodyType()){
                case KINEMATIC: bodyDef.type = BodyType.KINEMATIC; break;
                case STATIC: bodyDef.type = BodyType.STATIC; break;
                case DYNAMIC: bodyDef.type = BodyType.DYNAMIC; break;
            }

            //Determine collision shape
            Body body = this.world.createBody(bodyDef);
            rb.setRawBody(body);
            Circle2DCollider circleCollider = null;
            Box2DCollider boxCollider = null;

            //If statement structure assigns the object then checks to see if its null
            if ((circleCollider = go.getComponent(Circle2DCollider.class)) != null){
                //todo: implement addCircleCollider
                //shape.setRadius(circleCollider.getRadius());
            }
            if ((boxCollider = go.getComponent(Box2DCollider.class)) != null){
                addBox2DCollider(rb, boxCollider);
            }
        }
    }

    public void destroyGameObject(GameObject go){
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if (rb != null){
            if (rb.getRawBody() != null){
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
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

    public void addBox2DCollider(RigidBody2D rb, Box2DCollider boxCollider){
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null";

        PolygonShape shape = new PolygonShape();

        //mul by Half is to match with box2d physics engine, box2d draws shape from center
        Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = boxCollider.getOffset();
        Vector2f origin = new Vector2f(boxCollider.getOrigin());
        shape.setAsBox(halfSize.x,halfSize.y, new Vec2(offset.x,offset.y),0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        //todo: implement rb.getFriction();
        //fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = boxCollider.gameObject;
        //todo: implemennt rb.isSensor();
        //fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    public RaycastInfo raycast(GameObject requestingObject, Vector2f point1, Vector2f point2){
        RaycastInfo callback = new RaycastInfo(requestingObject);
        world.raycast(callback, new Vec2(point1.x,point1.y),new Vec2(point2.x,point2.y));
        return callback;
    }

}
