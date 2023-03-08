package components;

import engine.GameObject;
import engine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class DeathBlock extends Component{

    @Override
    public void start() {
        if (Window.isGameRunning()) {
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            if (spr != null) {
                spr.setColor(new Vector4f(0.0f,0.0f,0.0f, 0.0f));
            }
        } else {
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            if (spr != null) {
                spr.setColor(new Vector4f(1.0f,1.0f,1.0f, 1.0f));
            }
        }
    }

    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null && playerController.isDead()) {
            contact.setEnabled(false);
        }
        if (go.getComponent(Mob.class) != null) {
            go.destroy();
        }
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null && !playerController.isDead()) {
            playerController.die(true);
            //this.gameObject.destroy();
        }
    }
}
