package engine;

import components.*;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.Circle2DCollider;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
import util.AssetPool;

import javax.swing.plaf.nimbus.State;

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

    public static GameObject generatePlayer(){
        Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
        Spritesheet bigPlayerSprites = AssetPool.getSpriteSheet("assets/images/bigSpritesheet.png");
        GameObject player = generateSpriteObject(playerSprites.getSprite(0),0.25f,0.25f);

        float defaultFrameTime = 0.2f;

        //Little player animations
        AnimationState run = new AnimationState();
        run.title = "Run";
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4),0.1f);
        switchDirection.setLoop(false);

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(5),0.1f);
        jump.setLoop(false);

        //Big player Animations
        AnimationState bigRun = new AnimationState();
        bigRun.title = "Big Run";
        bigRun.addFrame(bigPlayerSprites.getSprite(0),defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1),defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2),defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(3),defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2),defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1),defaultFrameTime);
        bigRun.setLoop(true);

        AnimationState bigSwitchDirection = new AnimationState();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4),0.1f);
        bigSwitchDirection.setLoop(false);

        AnimationState bigIdle = new AnimationState();
        bigIdle.title = "Big Idle";
        bigIdle.addFrame(bigPlayerSprites.getSprite(0),0.1f);
        bigIdle.setLoop(false);

        AnimationState bigJump = new AnimationState();
        bigJump.title = "Big Jump";
        bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        //Fire player animations
        int fireOffset = 21;

        AnimationState fireRun = new AnimationState();
        fireRun.title = "Fire Run";
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        AnimationState fireSwitchDirection = new AnimationState();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        AnimationState fireIdle = new AnimationState();
        fireIdle.title = "Fire Idle";
        fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);

        AnimationState fireJump = new AnimationState();
        fireJump.title = "Fire Jump";
        fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        //Death animation
        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(6), 01.f);
        die.setLoop(false);

        //StateMachine

        StateMachine stateMachine = new StateMachine();
        //Little Player
        stateMachine.addState(run);
        stateMachine.addState(switchDirection);
        stateMachine.addState(idle);
        stateMachine.addState(jump);

        //Big player
        stateMachine.addState(bigRun);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigJump);

        //Fire Player
        stateMachine.addState(fireRun);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireJump);

        //Death
        stateMachine.addState(die);

        stateMachine.setDefaultState(idle.title);

        //add links between animations

        //Little player
        stateMachine.addStateTrigger(run.title,switchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(run.title,idle.title, "stopRunning");
        stateMachine.addStateTrigger(run.title,jump.title, "jump");
        stateMachine.addStateTrigger(switchDirection.title,idle.title, "stopRunning");
        stateMachine.addStateTrigger(switchDirection.title,run.title, "startRunning");
        stateMachine.addStateTrigger(switchDirection.title,jump.title, "jump");
        stateMachine.addStateTrigger(idle.title,run.title, "startRunning");
        stateMachine.addStateTrigger(idle.title,jump.title, "jump");
        stateMachine.addStateTrigger(jump.title,idle.title, "stopJumping");

        //big player
        stateMachine.addStateTrigger(bigRun.title,bigSwitchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(bigRun.title,bigIdle.title, "stopRunning");
        stateMachine.addStateTrigger(bigRun.title,bigJump.title, "jump");
        stateMachine.addStateTrigger(bigSwitchDirection.title,bigIdle.title, "stopRunning");
        stateMachine.addStateTrigger(bigSwitchDirection.title,bigRun.title, "startRunning");
        stateMachine.addStateTrigger(bigSwitchDirection.title,bigJump.title, "jump");
        stateMachine.addStateTrigger(bigIdle.title,bigRun.title, "startRunning");
        stateMachine.addStateTrigger(bigIdle.title,bigJump.title, "jump");
        stateMachine.addStateTrigger(bigJump.title,bigIdle.title, "stopJumping");

        //Fire player
        stateMachine.addStateTrigger(fireRun.title,fireSwitchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(fireRun.title,fireIdle.title, "stopRunning");
        stateMachine.addStateTrigger(fireRun.title,fireJump.title, "jump");
        stateMachine.addStateTrigger(fireSwitchDirection.title,fireIdle.title, "stopRunning");
        stateMachine.addStateTrigger(fireSwitchDirection.title,fireRun.title, "startRunning");
        stateMachine.addStateTrigger(fireSwitchDirection.title,fireJump.title, "jump");
        stateMachine.addStateTrigger(fireIdle.title,fireRun.title, "startRunning");
        stateMachine.addStateTrigger(fireIdle.title,fireJump.title, "jump");
        stateMachine.addStateTrigger(fireJump.title,fireIdle.title, "stopJumping");

        //Powerups
        stateMachine.addStateTrigger(run.title, bigRun.title, "powerup");
        stateMachine.addStateTrigger(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addStateTrigger(idle.title, bigIdle.title, "powerup");
        stateMachine.addStateTrigger(jump.title, bigJump.title, "powerup");
        stateMachine.addStateTrigger(bigRun.title, fireRun.title, "powerup");
        stateMachine.addStateTrigger(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addStateTrigger(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addStateTrigger(bigJump.title, fireJump.title, "powerup");

        //Damage
        stateMachine.addStateTrigger(bigRun.title, run.title, "damage");
        stateMachine.addStateTrigger(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addStateTrigger(bigIdle.title, idle.title, "damage");
        stateMachine.addStateTrigger(bigJump.title, jump.title, "damage");
        stateMachine.addStateTrigger(fireRun.title, bigRun.title, "damage");
        stateMachine.addStateTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addStateTrigger(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addStateTrigger(fireJump.title, bigJump.title, "damage");

        //die
        stateMachine.addStateTrigger(run.title, die.title, "die");
        stateMachine.addStateTrigger(switchDirection.title, die.title, "die");
        stateMachine.addStateTrigger(idle.title, die.title, "die");
        stateMachine.addStateTrigger(jump.title, die.title, "die");
        stateMachine.addStateTrigger(bigRun.title, run.title, "die");
        stateMachine.addStateTrigger(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addStateTrigger(bigIdle.title, idle.title, "die");
        stateMachine.addStateTrigger(bigJump.title, jump.title, "die");
        stateMachine.addStateTrigger(fireRun.title, bigRun.title, "die");
        stateMachine.addStateTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addStateTrigger(fireIdle.title, bigIdle.title, "die");
        stateMachine.addStateTrigger(fireJump.title, bigJump.title, "die");

        player.addComponent(stateMachine);

        PillboxCollider pb = new PillboxCollider();
        pb.width = 0.39f;
        pb.height =0.31f;
        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.DYNAMIC);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);

        player.addComponent(rb);
        player.addComponent(pb);
        player.addComponent(new PlayerController());
        player.name = "player";

        return player;
    }

    public static GameObject generateGoomba(){
        Spritesheet sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
        GameObject goomba = generateSpriteObject(sprites.getSprite(14),0.25f,0.25f);

        AnimationState walk = new AnimationState();
        walk.title = "Walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(sprites.getSprite(14), defaultFrameTime);
        walk.addFrame(sprites.getSprite(15), defaultFrameTime);
        walk.setLoop(true);

        AnimationState squash = new AnimationState();
        squash.title = "Squash";
        squash.addFrame(sprites.getSprite(16), 0.1f);
        squash.setLoop(false);


        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(squash);

        stateMachine.addStateTrigger(walk.title, squash.title, "squashMe");

        stateMachine.setDefaultState(walk.title);
        goomba.addComponent(stateMachine);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.DYNAMIC);
        rb.setMass(0.1f);
        rb.setFixedRotation(true);
        goomba.addComponent(rb);

        Circle2DCollider circle = new Circle2DCollider();
        circle.setRadius(0.12f);
        goomba.addComponent(circle);

        goomba.addComponent(new GoombaAi());

        return goomba;
    }

    public static GameObject generateQuestionBlock(){
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject questionBlock = generateSpriteObject(items.getSprite(0),0.25f,0.25f);

        AnimationState blink = new AnimationState();
        blink.title = "Blink";
        float defaultFrameTime = 0.23f;
        blink.addFrame(items.getSprite(0), 0.57f);
        blink.addFrame(items.getSprite(1), defaultFrameTime);
        blink.addFrame(items.getSprite(2), defaultFrameTime);
        blink.setLoop(true);

        AnimationState inactive = new AnimationState();
        inactive.title = "Inactive";
        inactive.addFrame(items.getSprite(3), 0.1f);
        inactive.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(blink);
        stateMachine.addState(inactive);

        stateMachine.setDefaultState(blink.title);

        stateMachine.addStateTrigger(blink.title,inactive.title,"setInactive");

        questionBlock.addComponent(stateMachine);
        questionBlock.addComponent(new QuestionBlock());

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.STATIC);
        questionBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f,0.25f));
        questionBlock.addComponent(b2d);
        questionBlock.addComponent(new Ground());

        return questionBlock;
    }

    public static GameObject generateBlockCoin(){
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject coin = generateSpriteObject(items.getSprite(7),0.25f,0.25f);

        AnimationState coinFlip = new AnimationState();
        coinFlip.title = "CoinFlip";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);

        stateMachine.setDefaultState(coinFlip.title);

        coin.addComponent(stateMachine);
        coin.addComponent(new QuestionBlock());

        coin.addComponent(new BlockCoin());

        return coin;
    }

    public static GameObject generateMushroom(){
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject mushroom = generateSpriteObject(items.getSprite(10),0.25f,0.25f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.DYNAMIC);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        mushroom.addComponent(rb);

        Circle2DCollider circleCollider = new Circle2DCollider();
        circleCollider.setRadius(0.14f);
        mushroom.addComponent(circleCollider);

        mushroom.addComponent(new MushroomAI());

        return mushroom;
    }

    public static GameObject generateFlower(){
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject flower = generateSpriteObject(items.getSprite(20),0.25f,0.25f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.STATIC);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        flower.addComponent(rb);

        Circle2DCollider circleCollider = new Circle2DCollider();
        circleCollider.setRadius(0.14f);
        flower.addComponent(circleCollider);

        flower.addComponent(new Flower());

        return flower;
    }

}
