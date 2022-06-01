package engine;

import components.Sprite;
import components.SpriteRenderer;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite spr, float sizeX, float sizeY){
        GameObject block = Window.getScene().createGameobject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(spr);
        block.addComponent(renderer);

        return block;
    }

}
