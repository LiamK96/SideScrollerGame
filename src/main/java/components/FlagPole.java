package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class FlagPole extends Component {

    private boolean isTop;

    public FlagPole(boolean isTop) {
        this.isTop = isTop;
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        Mob mob = obj.getComponent(Mob.class);
        if (mob != null) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = obj.getComponent(PlayerController.class);
        if (playerController != null) {
            playerController.playWinAnimation(this.gameObject);
        }
    }
}
