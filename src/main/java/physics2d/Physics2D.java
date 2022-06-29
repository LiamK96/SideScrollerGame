package physics2d;

import components.Ground;
import components.PlayerController;
import components.RigidBody;
import engine.GameObject;
import engine.Transform;
import engine.Window;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2d.components.Box2DCollider;
import physics2d.components.Circle2DCollider;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2D;
import renderer.DebugDraw;

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
            bodyDef.bullet = rb.isContinuousCollision();
            bodyDef.gravityScale = rb.getGravityScale();
            bodyDef.angularVelocity = rb.getAngularVelocity();
            bodyDef.userData = rb.gameObject; //all userdata will always be gameObject

            switch (rb.getBodyType()){
                case KINEMATIC: bodyDef.type = BodyType.KINEMATIC; break;
                case STATIC: bodyDef.type = BodyType.STATIC; break;
                case DYNAMIC: bodyDef.type = BodyType.DYNAMIC; break;
            }

            //Determine collision shape
            Body body = this.world.createBody(bodyDef);
            body.m_mass = rb.getMass();
            rb.setRawBody(body);

            Circle2DCollider circleCollider = null;
            Box2DCollider boxCollider = null;
            PillboxCollider pillboxCollider = null;

            //If statement structure assigns the object then checks to see if its null
            if ((circleCollider = go.getComponent(Circle2DCollider.class)) != null){
                addCircleCollider(rb, circleCollider);
            }
            if ((boxCollider = go.getComponent(Box2DCollider.class)) != null){
                addBox2DCollider(rb, boxCollider);
            }
            if ((pillboxCollider = go.getComponent(PillboxCollider.class)) != null){
                addPillboxCollider(rb, pillboxCollider);
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

    public void resetCircleCollider(RigidBody2D rb, Circle2DCollider circleCollider){
        Body body = rb.getRawBody();
        if (body == null){
            return;
        }
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++){
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rb, circleCollider);
        body.resetMassData();

    }

    public void resetBox2DCollider(RigidBody2D rb, Box2DCollider boxCollider){
        Body body = rb.getRawBody();
        if (body == null){
            return;
        }
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++){
            body.destroyFixture(body.getFixtureList());
        }

        addBox2DCollider(rb, boxCollider);
        body.resetMassData();

    }

    public void resetPillboxCollider(RigidBody2D rb, PillboxCollider pillboxCollider){
        Body body = rb.getRawBody();
        if (body == null){
            return;
        }
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++){
            body.destroyFixture(body.getFixtureList());
        }

        addPillboxCollider(rb, pillboxCollider);
        body.resetMassData();

    }

    public void addPillboxCollider(RigidBody2D rb, PillboxCollider pillbox){
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null";

        addBox2DCollider(rb, pillbox.getBox());
        addCircleCollider(rb, pillbox.getTopCircle());
        addCircleCollider(rb, pillbox.getBottomCircle());
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
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = boxCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    public void addCircleCollider(RigidBody2D rb, Circle2DCollider circleCollider){
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null";

        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    public RaycastInfo raycast(GameObject requestingObject, Vector2f point1, Vector2f point2){
        RaycastInfo callback = new RaycastInfo(requestingObject);
        world.raycast(callback, new Vec2(point1.x,point1.y),new Vec2(point2.x,point2.y));
        return callback;
    }

    private int fixtureListSize(Body body){
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null){
            size++;
            fixture = fixture.m_next;
        }
        return size;
    }

    public void setIsSensor(RigidBody2D rb){
        Body body = rb.getRawBody();
        if (body == null){
            return;
        }
        Fixture fixture = body.getFixtureList();
        while (fixture != null){
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }
    public void setNotSensor(RigidBody2D rb){
        Body body = rb.getRawBody();
        if (body == null){
            return;
        }
        Fixture fixture = body.getFixtureList();
        while (fixture != null){
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }

    public boolean isLocked(){
        return world.isLocked();
    }

    public Vector2f getGravity(){
        return new Vector2f(world.getGravity().x,world.getGravity().y);
    }

    public static boolean checkOnGround(GameObject gameObject, float innerPlayerWidth, float height){
        Vector2f raycastLeftBegin = new Vector2f(gameObject.transform.position);
        raycastLeftBegin.sub(innerPlayerWidth / 2.0f, 0.0f);
        Vector2f raycastLeftEnd = new Vector2f(raycastLeftBegin).add(0.0f, height);

        RaycastInfo infoLeft = Window.getPhysics().raycast(gameObject,raycastLeftBegin,raycastLeftEnd);

        Vector2f raycastRightBegin = new Vector2f(raycastLeftBegin).add(innerPlayerWidth,0.0f);
        Vector2f raycastRightEnd = new Vector2f(raycastLeftEnd).add(innerPlayerWidth,0.0f);

        RaycastInfo infoRight = Window.getPhysics().raycast(gameObject,raycastRightBegin,raycastRightEnd);

        //Used to see raycasts
        //DebugDraw.addLine2D(raycastLeftBegin,raycastLeftEnd, new Vector3f(1,0,0));
        //DebugDraw.addLine2D(raycastRightBegin,raycastRightEnd,new Vector3f(1,0,0));

        return  (infoLeft.hit && infoLeft.hitObj != null && infoLeft.hitObj.getComponent(Ground.class)!=null)
                || (infoRight.hit && infoRight.hitObj != null && infoRight.hitObj.getComponent(Ground.class)!=null);

    }

}
