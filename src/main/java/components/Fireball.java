package components;

import engine.GameObject;
import engine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2D;
import physics2d.components.RigidBody2D;

public class Fireball extends Component{

    private static int fireballCount = 0;

    private transient RigidBody2D rb;
    private transient float fireballSpeed = 1.7f;
    private transient float lifetime = 4.0f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f,3.1f);
    private transient boolean onGround = false;

    public transient boolean goingRight = false;

    public static boolean canSpawn(){
        return fireballCount < 4;
    }

    @Override
    public void start(){
        this.rb = this.gameObject.getComponent(RigidBody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        fireballCount++;
    }

    @Override
    public void update(float dt){
        lifetime -=dt;
        if (lifetime <= 0){
            disappear();
            return;
        }

        if (goingRight){
            velocity.x = fireballSpeed;
        } else {
            velocity.x = -fireballSpeed;
        }

        checkOnGround();
        if (onGround){
            this.acceleration.y = 1.5f;
            this.velocity.y = 2.5f;
        } else {
            //this.velocity.x = 0;
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rb.setVelocity(velocity);
    }

    private void checkOnGround(){
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.09f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal){
        if (obj.getComponent(PlayerController.class) != null
                || obj.getComponent(Fireball.class) != null){
            contact.setEnabled(false);
        }
    }

    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal){
        if (Math.abs(contactNormal.x) > 0.8f){
            this.goingRight = contactNormal.x < 0;
        }
    }

    public void disappear(){
        fireballCount--;
        this.gameObject.destroy();
    }
}
