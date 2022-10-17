package engine;

import components.*;
import components.enums.Direction;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2d.components.Box2DCollider;
import physics2d.components.Circle2DCollider;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
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

    public static GameObject generatePlayer(){
        Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
        Spritesheet bigPlayerSprites = AssetPool.getSpriteSheet("assets/images/bigSpritesheet.png");
        GameObject player = generateSpriteObject(playerSprites.getSprite(0),0.25f,0.25f);

        float defaultFrameTime = 0.2f;

        //Little player animations
        AnimationState run = new AnimationState();
        run.title = "Run";
        //run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
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
        //bigRun.addFrame(bigPlayerSprites.getSprite(0),defaultFrameTime);
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
        //fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), defaultFrameTime);
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

        RigidBody2D rb = createRigidBody(BodyType.DYNAMIC, 25.0f);
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

        goomba.addComponent(createRigidBody(BodyType.DYNAMIC, 0.1f));

        goomba.addComponent(createCircleCollider(0.12f));

        goomba.addComponent(new GoombaAi());
        goomba.addComponent(new Mob());

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

        questionBlock.addComponent(createRigidBody(BodyType.STATIC));

        questionBlock.addComponent(createBoxCollider(new Vector2f(0.25f,0.25f)));

        questionBlock.addComponent(new Ground());

        return questionBlock;
    }

    public static GameObject generateDeathBlock(){
        Spritesheet deathBlocks = AssetPool.getSpriteSheet("assets/images/blendImage1.png");
        GameObject deathBlock = generateSpriteObject(deathBlocks.getSprite(0),0.25f,0.25f);

        deathBlock.addComponent(createRigidBody(BodyType.STATIC));
        deathBlock.addComponent(createBoxCollider(new Vector2f(0.25f,0.25f)));
        deathBlock.addComponent(new DeathBlock());

        return deathBlock;
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

        mushroom.addComponent(createRigidBody(BodyType.DYNAMIC));

        mushroom.addComponent(createCircleCollider(0.14f));

        mushroom.addComponent(new MushroomAI());
        mushroom.addComponent(new Mob());

        return mushroom;
    }

    public static GameObject generateFlower(){
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject flower = generateSpriteObject(items.getSprite(20),0.25f,0.25f);

        flower.addComponent(createRigidBody(BodyType.STATIC));

        flower.addComponent(createCircleCollider(0.14f));

        flower.addComponent(new Flower());

        return flower;
    }

    public static GameObject generatePipe(Direction direction){
        Spritesheet pipes = AssetPool.getSpriteSheet("assets/images/spritesheets/pipes.png");

        int spriteIndex = -1;
        switch (direction){
            case Down:
                spriteIndex = 0;
                break;
            case Up:
                spriteIndex = 1;
                break;
            case Right:
                spriteIndex = 2;
                break;
            case Left:
                spriteIndex = 3;
                break;
        }

        GameObject pipe = generateSpriteObject(pipes.getSprite(spriteIndex),0.5f,0.5f);

        pipe.addComponent(createRigidBody(BodyType.STATIC));

        pipe.addComponent(createBoxCollider(new Vector2f(0.5f,0.5f)));

        pipe.addComponent(new Pipe(direction));
        pipe.addComponent(new Ground());

        return pipe;
    }

    public static GameObject generateTurtle(){
        Spritesheet turtleSprites = AssetPool.getSpriteSheet("assets/images/turtle.png");
        GameObject turtle = generateSpriteObject(turtleSprites.getSprite(0),0.25f,0.35f);

        AnimationState walk = new AnimationState();
        walk.title = "Walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(turtleSprites.getSprite(0), defaultFrameTime);
        walk.addFrame(turtleSprites.getSprite(1), defaultFrameTime);
        walk.setLoop(true);

        AnimationState turtleShell = new AnimationState();
        turtleShell.title = "TurtleShellSpin";
        turtleShell.addFrame(turtleSprites.getSprite(2), 0.1f);
        turtleShell.setLoop(false);


        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(turtleShell);

        stateMachine.addStateTrigger(walk.title, turtleShell.title, "squashMe");

        stateMachine.setDefaultState(walk.title);
        turtle.addComponent(stateMachine);

        turtle.addComponent(createRigidBody(BodyType.DYNAMIC,0.1f));

        turtle.addComponent(createCircleCollider(0.13f,
                new Vector2f(0.0f, -0.05f)));

        turtle.addComponent(new TurtleAi());
        turtle.addComponent(new Mob());

        return turtle;
    }

    public static GameObject generateFlagPole(){
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject flagPole = generateSpriteObject(items.getSprite(33),0.25f,0.25f);

        flagPole.addComponent(createRigidBody(BodyType.DYNAMIC));

        flagPole.addComponent(createBoxCollider(new Vector2f(0.1f,0.25f),
                new Vector2f(-0.075f, 0.0f)));

        flagPole.addComponent(new FlagPole(false));

        return flagPole;
    }

    public static GameObject generateFlagTop(){
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject flagTop = generateSpriteObject(items.getSprite(6),0.25f,0.25f);

        flagTop.addComponent(createRigidBody(BodyType.DYNAMIC));

        flagTop.addComponent(createBoxCollider(new Vector2f(0.1f,0.25f),
                new Vector2f(-0.075f,0.0f)));

        flagTop.addComponent(new FlagPole(true));

        return flagTop;
    }

    public static GameObject generateFireball(Vector2f position){
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject fireball = generateSpriteObject(items.getSprite(32),0.18f,0.18f);
        fireball.transform.position = position;

        fireball.addComponent(createRigidBody(BodyType.DYNAMIC));

        fireball.addComponent(createCircleCollider(0.08f));

        fireball.addComponent(new Fireball());

        return fireball;
    }

    private static RigidBody2D createRigidBody(BodyType bodyType){
         return createRigidBody(bodyType, 0.0f, true,false);
    }

    private static RigidBody2D createRigidBody(BodyType bodyType, float mass){
        return createRigidBody(bodyType,mass, true, false);
    }

    private static RigidBody2D createRigidBody(BodyType bodyType,
                                        float mass, boolean fixedRotation,
                                        boolean continuousCollision){
        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(bodyType);
        rb.setMass(mass);
        rb.setFixedRotation(fixedRotation);
        rb.setContinuousCollision(continuousCollision);
        return rb;
    }

    private static Box2DCollider createBoxCollider(Vector2f halfSize){
        return createBoxCollider(halfSize, new Vector2f());
    }

    private static Box2DCollider createBoxCollider(Vector2f halfSize, Vector2f offset){
        Box2DCollider boxCollider = new Box2DCollider();
        boxCollider.setHalfSize(halfSize);
        boxCollider.setOffset(offset);
        return boxCollider;
    }

    private static Circle2DCollider createCircleCollider(float radius){
        return createCircleCollider(radius, new Vector2f());
    }

    private static Circle2DCollider createCircleCollider(float radius, Vector2f offset){
        Circle2DCollider circleCollider = new Circle2DCollider();
        circleCollider.setRadius(radius);
        circleCollider.setOffset(offset);
        return circleCollider;
    }

}
