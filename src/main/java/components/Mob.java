package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Mob extends Component {

    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal){
        GameBorder gameBorder = go.getComponent(GameBorder.class);
        if (gameBorder != null){
            contact.setEnabled(false);
        }
    }
}
