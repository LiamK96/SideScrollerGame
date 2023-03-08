package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;

public abstract class Block extends Component {

    private transient boolean bopGoingUp = true;
    private transient boolean doBopAnimation = false;
    private transient Vector2f bopStart;
    private transient Vector2f bopStopLocation;
    private transient boolean active = true;

    public float bopSpeed = 0.6f;

    @Override
    public void start() {
        this.bopStart = new Vector2f(this.gameObject.transform.position);
        this.bopStopLocation = new Vector2f(bopStart).add(0.0f,0.02f);
    }

    @Override
    public void update(float dt) {
        if (doBopAnimation) {
            if (bopGoingUp) {
                if (this.gameObject.transform.position.y < bopStopLocation.y) {
                    this.gameObject.transform.position.y += bopSpeed * dt;
                } else {
                    bopGoingUp = false;
                }
            } else {
                if (this.gameObject.transform.position.y > bopStart.y){
                    this.gameObject.transform.position.y -= bopSpeed * dt;
                } else {
                    this.gameObject.transform.position.y = bopStart.y;
                    bopGoingUp = true;
                    doBopAnimation  =false;
                }
            }
        }
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (active && playerController != null && contactNormal.y < -0.8f) {
            doBopAnimation = true;
            AssetPool.getSound("assets/sounds/bump.ogg").play();
            playerHit(playerController);
        }
    }

    public void setInactive() {
        this.active = false;
    }

    public void setActive() {
        this.active = true;
    }

    public abstract void playerHit(PlayerController playerController);
}
