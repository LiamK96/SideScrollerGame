package components;

import engine.*;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2d.Physics2D;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
import scenes.LevelSceneInitializer;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {




    private enum PlayerState {
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


    private transient RigidBody2D rb;
    private transient StateMachine stateMachine;
    private transient float bigBoostFactor = 1.05f;
    private transient float groundDebounceTime = 0.1f;
    private transient float groundDebounce = 0.0f;
    private transient float playerWidth = 0.25f;
    private transient float hurtInvicibilityTimeLeft = 0;
    private transient float hurtInvicibilityTime = 1.4f;
    private transient float deadMinHeight = 0;
    private transient float deadMaxHeight = 0;
    private transient float blinkTime = 0.0f;
    private transient int jumpTime = 0;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();



    private transient boolean isDead = false;
    private transient boolean deadGoingUp = true;
    private transient int enemyBounce = 0;
    private transient SpriteRenderer spr;

    private transient boolean playWinAnimation = false;
    private transient float timeToCastle = 4.5f;
    private transient float walkTime = 2.2f;

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb.setGravityScale(0.0f);
        this.spr = gameObject.getComponent(SpriteRenderer.class);
    }

    @Override
    public void update(float dt) {
        //System.out.println(playWinAnimation);
        if (playWinAnimation) {
            checkOnGround();
            if (!onGround) {
                gameObject.transform.scale.x = -0.25f;
                gameObject.transform.position.y -= dt;
                stateMachine.trigger("stopRunning");
                stateMachine.trigger("stopJumping");
            } else {
                if (this.walkTime > 0) {
                    gameObject.transform.scale.x = 0.25f;
                    gameObject.transform.position.x += dt;
                    stateMachine.trigger("startRunning");
                }
                if (!AssetPool.getSound("assets/sounds/stage_clear.ogg").isPlaying()) {
                    AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
                    AssetPool.getSound("assets/sounds/stage_clear.ogg").play();
                }
                timeToCastle -= dt;
                walkTime -= dt;

                if (timeToCastle <= 0) {

                    EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
                }
            }
            return;
        }
        if (isDead) {
            if (this.gameObject.transform.position.y < deadMaxHeight && deadGoingUp) {
                this.gameObject.transform.position.y += dt * walkSpeed / 2.0f;
            } else if (this.gameObject.transform.position.y >= deadMaxHeight && deadGoingUp) {
                deadGoingUp = false;
            } else if (!deadGoingUp && this.gameObject.transform.position.y > deadMinHeight) {
                this.rb.setBodyType(BodyType.KINEMATIC);
                this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
                this.velocity.y += this.acceleration.y * dt;
                this.velocity.y = Math.max(
                        Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
                this.rb.setVelocity(this.velocity);
                this.rb.setAngularVelocity(0);
            } else if (!deadGoingUp && this.gameObject.transform.position.y <= deadMinHeight) {
                if (!AssetPool.getSound("assets/sounds/mario_die.ogg").isPlaying()) {
                    Window.changeScene(new LevelSceneInitializer());
                }
            }
            return;
        }

        if (hurtInvicibilityTimeLeft > 0) {
            hurtInvicibilityTimeLeft -= dt;
            blinkTime -= dt;
            if (blinkTime <= 0) {
                blinkTime = 0.2f;
                if (spr.getColor().w == 1) {
                    spr.setColor(new Vector4f(1, 1, 1, 0));
                } else {
                    spr.setColor(new Vector4f(1, 1, 1, 1));
                }
            } else {
                if (spr.getColor().w == 0) {
                    spr.setColor(new Vector4f(1, 1, 1, 1));
                }
            }
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            if (this.velocity.x < 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x += slowDownForce;
            } else {
                stateMachine.trigger("startRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
            this.gameObject.transform.scale.x = -playerWidth;
            this.acceleration.x = -walkSpeed;

            if (this.velocity.x > 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x -= slowDownForce;
            } else {
                stateMachine.trigger("startRunning");
            }
        } else {
            this.acceleration.x = 0;
            if (this.velocity.x > 0) {
                this.velocity.x = Math.max(0, this.velocity.x - this.slowDownForce);
            } else if (this.velocity.x < 0) {
                this.velocity.x = Math.min(0, this.velocity.x + this.slowDownForce);
            }
            if (this.velocity.x == 0) {
                this.stateMachine.trigger("stopRunning");
            }
        }

        if (KeyListener.keyBeginPress(GLFW_KEY_E) && playerState == PlayerState.Fire
                && Fireball.canSpawn()) {
            Vector2f position = new Vector2f(this.gameObject.transform.position)
                    .add(this.gameObject.transform.scale.x > 0
                            ? new Vector2f(0.26f, 0.0f)
                            : new Vector2f(-0.26f, 0.0f));
            GameObject fireball = Prefabs.generateFireball(position);
            fireball.getComponent(Fireball.class).goingRight = this.gameObject.transform.scale.x > 0;
            Window.getScene().addGameObjectToScene(fireball);

        }

        checkOnGround();
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)
                && (jumpTime > 0 || onGround || groundDebounce > 0)) {
            if ((onGround || groundDebounce > 0) && jumpTime == 0) {
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                jumpTime = 28;
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                this.velocity.y = ((jumpTime / 2.2f) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }
            groundDebounce = 0;
        } else if (enemyBounce > 0) {
            enemyBounce--;
            this.velocity.y = ((enemyBounce / 2.2f) * jumpBoost);
        } else if (!onGround) {
            if (this.jumpTime > 0) {
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

        if (!onGround) {
            stateMachine.trigger("jump");
        } else {
            stateMachine.trigger("stopJumping");
        }
    }

    public void checkOnGround() {
        float innerPlayerWidth = this.playerWidth * 0.6f;
        float yVal = (playerState == PlayerState.Small) ? -0.14f : -0.24f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    public void playWinAnimation(GameObject flagPole) {
        if (!playWinAnimation) {
            playWinAnimation = true;
            velocity.zero();
            acceleration.zero();
            rb.setVelocity(velocity);
            rb.setAsSensor();
            rb.setBodyType(BodyType.STATIC);
            gameObject.transform.position.x = flagPole.transform.position.x;
            AssetPool.getSound("assets/sounds/flagpole.ogg").play();
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        if (isDead) return;

        if (collidingObject.getComponent(Ground.class) != null) {
            if (Math.abs(contactNormal.x) > 0.8f) {
                this.velocity.x = 0;
            } else if (contactNormal.y > 0.8f) {
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }
    }

    public void stopMovementLeft() {
        this.velocity.x = 0;
        this.acceleration.x = 0;
    }

    public void powerUp() {
        powerUp(false);
    }

    public void powerUp(boolean isUsingFlower) {
        if (playerState == PlayerState.Small) {
            if (isUsingFlower) {
                playerState = PlayerState.Fire;
                stateMachine.trigger("powerup");
            } else {
                playerState = PlayerState.Big;
            }
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
            gameObject.transform.scale.y = 0.42f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null) {
                jumpBoost *= bigBoostFactor;
                walkSpeed *= bigBoostFactor;
                pb.setHeight(0.63f);
            }
        } else if (playerState == PlayerState.Big) {
            playerState = PlayerState.Fire;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
        }
        stateMachine.trigger("powerup");
    }

    public void die() {
        die(false);
    }

    public void die(boolean isTouchingDeathBlock) {
        this.gameObject.transform.zIndex++;
        stateMachine.trigger("die");
        if (playerState == PlayerState.Small || isTouchingDeathBlock) {
            this.velocity.zero();
            this.acceleration.zero();
            this.rb.setVelocity(new Vector2f());
            this.isDead = true;
            this.rb.setAsSensor();
            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
            AssetPool.getSound("assets/sounds/mario_die.ogg").play();
            deadMaxHeight = this.gameObject.transform.position.y + 0.3f;
            this.rb.setBodyType(BodyType.STATIC);
            if (gameObject.transform.position.y > 0) {
                deadMinHeight = -0.25f;
            }
        } else if (this.playerState == PlayerState.Big) {
            this.playerState = PlayerState.Small;
            gameObject.transform.scale.y = 0.25f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null) {
                jumpBoost /= bigBoostFactor;
                walkSpeed /= bigBoostFactor;
                pb.setHeight(0.31f);
            }
            hurtInvicibilityTimeLeft = hurtInvicibilityTime;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();
        } else if (this.playerState == PlayerState.Fire) {
            this.playerState = PlayerState.Big;
            hurtInvicibilityTimeLeft = hurtInvicibilityTime;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();
        }
    }

    public void setPosition(Vector2f newPos) {
        this.gameObject.transform.position.set(newPos);
        this.rb.setPosition(newPos);
    }

    public void enemyBounce() {
        this.enemyBounce = 8;
    }

    public boolean isSmall() {
        return this.playerState == PlayerState.Small;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isHurtInvincible() {
        return this.hurtInvicibilityTimeLeft > 0 || playWinAnimation;
    }

    public boolean isInvincible() {
        return this.playerState == PlayerState.Invincible
                || this.hurtInvicibilityTimeLeft > 0 || playWinAnimation;
    }

    public boolean hasWon() {
        return playWinAnimation;
    }
}