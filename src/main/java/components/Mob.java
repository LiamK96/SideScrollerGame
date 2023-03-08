package components;

import engine.GameObject;
import engine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Mob extends Component {

    @Override
    public void update(float dt) {
        if (this.gameObject.transform.position.x < Window.getScene().getCamera().position.x - 0.5f) {
            this.gameObject.destroy();
        }
    }

    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal) {
        GameBorder gameBorder = go.getComponent(GameBorder.class);
        if (gameBorder != null) {
            contact.setEnabled(false);
        }
    }
}
