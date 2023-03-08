package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class GameBorder extends Component {

    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController == null) {
            contact.setEnabled(false);
        }
    }
}
