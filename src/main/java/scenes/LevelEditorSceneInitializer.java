package scenes;

import components.*;
import engine.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
import util.AssetPool;

import java.io.File;
import java.util.Collection;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private Spritesheet sprites;

    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer(){

    }

    @Override
    public void init(Scene scene){

        sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");

        levelEditorStuff = scene.createGameobject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));

        scene.addGameObjectToScene(levelEditorStuff);

    }

    public void loadResources(Scene scene){
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16,16,81,0));
        AssetPool.addSpriteSheet("assets/images/spritesheets/pipes.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/pipes.png"),
                        16,16,7,0));
        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16,16,26,0));
        AssetPool.addSpriteSheet("assets/images/bigSpritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/bigSpritesheet.png"),
                        16,32,42,0));
        AssetPool.addSpriteSheet("assets/images/turtle.png",
                new Spritesheet(AssetPool.getTexture("assets/images/turtle.png"),
                        16,24,4,0));
        AssetPool.addSpriteSheet("assets/images/items.png",
                new Spritesheet(AssetPool.getTexture("assets/images/items.png"),
                        16,16,33,0));
        AssetPool.addSpriteSheet("assets/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24,48,3,0));
        AssetPool.getTexture("assets/images/blendImage1.png");

        AssetPool.addSound("assets/sounds/1-up.ogg", false);
        AssetPool.addSound("assets/sounds/bowserfalls.ogg", false);
        AssetPool.addSound("assets/sounds/bowserfire.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/fireball.ogg", false);
        AssetPool.addSound("assets/sounds/fireworks.ogg", false);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/jump-super.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/vine.ogg", false);
        AssetPool.addSound("assets/sounds/warning.ogg", false);
        AssetPool.addSound("assets/sounds/world_clear.ogg", false);

        for (GameObject go : scene.getGameObjects()){
            if (go.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr.getTexture()!=null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }

            if (go.getComponent(StateMachine.class) != null){
                StateMachine stateMachine = go.getComponent(StateMachine.class);
                stateMachine.refeshTextures();
            }
        }

    }

    @Override
    public void imgui(){

        ImGui.begin("Level Editor Stuff");
        if (ImGui.collapsingHeader("Mouse Stuff")){
            ImGui.text("Mouse is dragging: "+MouseListener.isDragging());
            ImGui.text("Mouse World Coords ("+MouseListener.getWorldX()+" : "+MouseListener.getWorldY()+")");
            ImGui.text("Mouse Screen Coords ("+MouseListener.getScreenX()+" : "+MouseListener.getScreenY()+")");
            ImGui.text("Current ("+(MouseListener.getX()-MouseListener.get().gameViewportPos.x)+" : "
                    +(MouseListener.getY()-MouseListener.get().gameViewportPos.y)+")");
            ImGui.text("GameViewPort Pos Y: "+MouseListener.get().gameViewportPos.y);
            ImGui.text("GameViewPort Size Y: "+MouseListener.get().gameViewportSize.y);
            ImGui.text("Inverse View:\n" +Window.getScene().getCamera().getInverseView());
            ImGui.text("Inverse Projection:\n" +Window.getScene().getCamera().getInverseProjection());
        }


        levelEditorStuff.imgui();
        ImGui.end();


        ImGui.begin("Objects");

        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Blocks")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;

                //Generate available tiles
                for (int i = 0; i < sprites.size(); i++) {

                    if (i == 34) continue;
                    if (i >= 38 && i < 61) continue;

                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    //Push Custom ID to sprite, since without this they all share the spritesheet id
                    ImGui.pushID(i);

                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f); //old (sprite, spriteWidth, spriteHeight)
                        RigidBody2D rb = new RigidBody2D();
                        rb.setBodyType(BodyType.STATIC);
                        object.addComponent(rb);
                        Box2DCollider b2d = new Box2DCollider();
                        b2d.setHalfSize(new Vector2f(0.25f,0.25f));
                        object.addComponent(b2d);
                        object.addComponent(new Ground());
                        if (i ==12){
                            //todo: add BreakableBrick
                            //object.addComponent(new BreakableBrick());
                        }
                        //Attach to mouse cursor
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    //Pop custom ID
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Prefabs")){

                Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 4;
                float spriteHeight = sprite.getHeight() * 4;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generatePlayer();

                    //Attach to mouse cursor
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();

                Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");

                sprite = items.getSprite(0);
                spriteWidth = sprite.getWidth() * 4;
                spriteHeight = sprite.getHeight() * 4;
                id = sprite.getTexId();
                 texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateQuestionBlock();

                    //Attach to mouse cursor
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Sounds")){
                Collection<Sound> sounds = AssetPool.getAllSounds();

                ImVec2 windowSize= new ImVec2(ImGui.getWindowPos());
                for (Sound sound : sounds){
                    File tmp = new File(sound.getFilepath());
                    if (ImGui.button(tmp.getName())){
                        if (!sound.isPlaying()){
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }
                    if (ImGui.getContentRegionAvailX() > 100){
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }
        ImGui.end();
    }



}
