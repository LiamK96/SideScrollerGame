package engine;

import components.*;
import util.AssetPool;

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

    public static GameObject generateMario(){
        Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0),0.25f,0.25f);

        AnimationState run = new AnimationState();
        run.title = "run";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        mario.addComponent(stateMachine);

        return mario;
    }

    public static GameObject generateQuestionBlock(){
        Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject questionBlock = generateSpriteObject(playerSprites.getSprite(0),0.25f,0.25f);

        AnimationState blink = new AnimationState();
        blink.title = "blink";
        float defaultFrameTime = 0.23f;
        blink.addFrame(playerSprites.getSprite(0), 0.57f);
        blink.addFrame(playerSprites.getSprite(1), defaultFrameTime);
        blink.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        blink.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(blink);
        stateMachine.setDefaultState(blink.title);
        questionBlock.addComponent(stateMachine);

        return questionBlock;
    }

}
