package components;

import engine.Component;
import engine.GameObject;

public class FontRenderer extends Component {

    @Override
    public void start(){
        if (gameObject.getComponent(SpriteRenderer.class) != null){
            System.out.println("Found sprite renderer");
        }
    }

    @Override
    public void update(float dt) {

    }
}
