package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class DeathBlock extends Component{


    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal){
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null && playerController.isDead()){
            contact.setEnabled(false);
        }
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal){
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null){
            playerController.die(true);
            this.gameObject.destroy();
        }
    }
}
