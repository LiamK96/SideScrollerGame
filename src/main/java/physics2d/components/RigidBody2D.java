package physics2d.components;

import components.Component;
import engine.Window;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;
import physics2d.enums.BodyType;

public class RigidBody2D extends Component {

    private Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 0.0f;
    private BodyType bodyType = BodyType.DYNAMIC;
    private float friction = 0.1f;
    private float angularVelocity = 0.0f;
    private float gravityScale = 1.0f;
    private boolean isSensor = false;

    private boolean fixedRotation = false;
    private boolean continuousCollision = false;

    private transient Body rawBody = null;

    @Override
    public void update(float dt) {
        if (this.bodyType == BodyType.DYNAMIC || this.bodyType == BodyType.KINEMATIC) {
            //match physics engine change to game engine change.
            if (rawBody != null) {
                this.gameObject.transform.position.set(rawBody.getPosition().x, rawBody.getPosition().y);
                this.gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
                Vec2 vel = rawBody.getLinearVelocity();
                this.velocity.set(vel.x,vel.y);
            }
        } else if (this.bodyType == BodyType.STATIC) {
            this.rawBody.setTransform(
                    new Vec2(this.gameObject.transform.position.x,this.gameObject.transform.position.y)
                    ,this.gameObject.transform.rotation);
        }
    }

    public void addVelocity(Vector2f force) {
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(force.x, force.y));
        }
    }

    public void addImpulse(Vector2f impulse) {
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(impulse.x,impulse.y), rawBody.getWorldCenter());
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (rawBody != null) {
            this.rawBody.setLinearVelocity(new Vec2(velocity.x,velocity.y));
        }
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null){
            this.rawBody.setAngularVelocity(angularVelocity);
        }
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) {
            this.rawBody.setGravityScale(gravityScale);
        }
    }

    public boolean isSensor() {
        return isSensor;
    }

    public void setAsSensor(){
        isSensor = true;
        if (rawBody != null) {
            Window.getPhysics().setIsSensor(this);
        }
    }
    public void setAsNotSensor() {
        isSensor = false;
        if (rawBody != null) {
            Window.getPhysics().setNotSensor(this);
        }
    }

    public void setPosition(Vector2f newPos) {
        if (rawBody != null) {
            rawBody.setTransform(new Vec2(newPos.x, newPos.y), gameObject.transform.rotation);
        }
    }

}
