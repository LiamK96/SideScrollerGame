package components;

import engine.GameObject;
import engine.KeyListener;
import engine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.RaycastInfo;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2D;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {

    private enum PlayerState{
        Small,
        Big,
        Fire,
        Invincible
    }

    public float walkSpeed = 1.9f;
    public float jumpBoost = 1.0f;
    public float jumpImpulse = 3.0f;
    public float slowDownForce = 0.05f;
    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private PlayerState playerState = PlayerState.Small;

    public transient boolean onGround = false;
    private transient float groundDebounce = 0.0f;
    private transient final float groundDebounceTime = 0.1f;
    private transient RigidBody2D rb;
    private transient StateMachine stateMachine;
    private final transient float BIG_BOOST_FACTOR = 1.05f;
    private transient float playerWidth = 0.25f;
    private transient int jumpTime = 0;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean isDead = false;
    private transient int enemyBounce = 0;

    @Override
    public void start(){
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb.setGravityScale(0.0f);
    }

    @Override
    public void update(float dt){
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)){
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            if (this.velocity.x < 0){
                this.stateMachine.trigger("switchDirection");
                this.velocity.x += slowDownForce;
            } else {
                stateMachine.trigger("startRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)){
            this.gameObject.transform.scale.x = -playerWidth;
            this.acceleration.x = -walkSpeed;

            if (this.velocity.x > 0){
                this.stateMachine.trigger("switchDirection");
                this.velocity.x -= slowDownForce;
            } else {
                stateMachine.trigger("startRunning");
            }
        } else {
            this.acceleration.x = 0;
            if (this.velocity.x > 0){
                this.velocity.x = Math.max(0, this.velocity.x - this.slowDownForce);
            } else if (this.velocity.x < 0){
                this.velocity.x = Math.min(0, this.velocity.x + this.slowDownForce);
            }
            if (this.velocity.x == 0){
                this.stateMachine.trigger("stopRunning");
            }
        }

        checkOnGround();
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)
                && (jumpTime > 0 || onGround || groundDebounce > 0)){
            if ((onGround || groundDebounce > 0) && jumpTime == 0){
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                jumpTime = 28;
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0){
                jumpTime--;
                this.velocity.y = ((jumpTime / 2.2f) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }
            groundDebounce = 0;
        } else if (enemyBounce > 0){
            //todo: implement enemyBounce
        } else if (!onGround){
            if (this.jumpTime > 0){
                this.velocity.y *= 0.35f;
                this.jumpTime = 0;
            }
            groundDebounce -= dt;
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        } else {
            this.velocity.y = 0;
            this.acceleration.y = 0;
            this.groundDebounce = groundDebounceTime;
        }

        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;

        this.velocity.x += this.acceleration.x * dt;
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0);

        if (!onGround){
            stateMachine.trigger("jump");
        } else {
            stateMachine.trigger("stopJumping");
        }
    }

    public void checkOnGround(){
        Vector2f raycastLeftBegin = new Vector2f(this.gameObject.transform.position);
        float innerPlayerWidth = this.playerWidth * 0.6f;
        raycastLeftBegin.sub(innerPlayerWidth / 2.0f, 0.0f);
        float yVal = (playerState == PlayerState.Small) ? -0.14f : -0.24f;
        Vector2f raycastLeftEnd = new Vector2f(raycastLeftBegin).add(0.0f, yVal);

        RaycastInfo infoLeft = Window.getPhysics().raycast(gameObject,raycastLeftBegin,raycastLeftEnd);

        Vector2f raycastRightBegin = new Vector2f(raycastLeftBegin).add(innerPlayerWidth,0.0f);
        Vector2f raycastRightEnd = new Vector2f(raycastLeftEnd).add(innerPlayerWidth,0.0f);

        RaycastInfo infoRight = Window.getPhysics().raycast(gameObject,raycastRightBegin,raycastRightEnd);

        onGround = (infoLeft.hit && infoLeft.hitObj != null && infoLeft.hitObj.getComponent(Ground.class)!=null)
                || (infoRight.hit && infoRight.hitObj != null && infoRight.hitObj.getComponent(Ground.class)!=null);

        //Used to see raycasts
        //DebugDraw.addLine2D(raycastLeftBegin,raycastLeftEnd, new Vector3f(1,0,0));
        //DebugDraw.addLine2D(raycastRightBegin,raycastRightEnd,new Vector3f(1,0,0));
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal){
        if (isDead) return;

        if (collidingObject.getComponent(Ground.class) != null){
            if (Math.abs(contactNormal.x)>0.8f){
                this.velocity.x = 0;
            } else if (contactNormal.y > 0.8f){
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }
    }

    public void powerUp(){
        if (playerState == PlayerState.Small){
            playerState = PlayerState.Big;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
            gameObject.transform.scale.y = 0.42f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null){
                jumpBoost *= BIG_BOOST_FACTOR;
                walkSpeed *= BIG_BOOST_FACTOR;
                pb.setHeight(0.63f);
            }
        } else if (playerState == PlayerState.Big){
            playerState = PlayerState.Fire;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
        }
        stateMachine.trigger("powerup");
    }

    public boolean isSmall(){
        return this.playerState == PlayerState.Small;
    }
}