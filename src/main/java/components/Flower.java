package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.RigidBody2D;
import util.AssetPool;

public class Flower extends Component {

    private transient RigidBody2D rb;

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        this.rb.setAsSensor();
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null) {
            playerController.powerUp(true);
            this.gameObject.destroy();
        }
    }

}
