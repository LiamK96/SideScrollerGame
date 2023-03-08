package scenes;

import components.*;
import components.enums.Direction;
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
import java.util.HashMap;
import java.util.Map;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private Spritesheet sprites;

    private GameObject levelEditorComponents;

    public LevelEditorSceneInitializer() {

    }

    @Override
    public void init(Scene scene) {

        sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");

        levelEditorComponents = scene.createGameobject("LevelEditor");
        levelEditorComponents.setNoSerialize();
        levelEditorComponents.addComponent(new MouseControls());
        levelEditorComponents.addComponent(new KeyControls());
        levelEditorComponents.addComponent(new GridLines());
        levelEditorComponents.addComponent(new EditorCamera(scene.getCamera()));
        //levelEditorComponents.addComponent(new GizmoSystem(gizmos));

        scene.addGameObjectToScene(levelEditorComponents);

    }

    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16,16,81,0));
        AssetPool.addSpriteSheet("assets/images/spritesheets/pipes.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/pipes.png"),
                        32,32,4,0));
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
                        16,16,34,0));
        AssetPool.addSpriteSheet("assets/images/spritesheets/icons.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/icons.png"),
                        32,32,16,0));
        AssetPool.addSpriteSheet("assets/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24,48,3,0));
        AssetPool.addSpriteSheet("assets/images/blendImage1.png",
                new Spritesheet(AssetPool.getTexture("assets/images/blendImage1.png"),
                        16,16,1,0));

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

        for (GameObject go : scene.getGameObjects()) {
            if (go.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr.getTexture()!=null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }

            if (go.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = go.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }

    }

    @Override
    public void imgui() {

        ImGui.begin("Level Editor Stuff");
        if (ImGui.collapsingHeader("Mouse Stuff")) {
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

        ImGui.text("Camera Zoom = "+ Window.getScene().getCamera().getZoom());
        levelEditorComponents.imgui();
        ImGui.end();


        ImGui.begin("Objects");

        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Solid Blocks")) {

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

                    if (ImGui.imageButton(id, spriteWidth, spriteHeight,
                            texCoords[2].x, texCoords[0].y,
                            texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f); //old (sprite, spriteWidth, spriteHeight)
                        RigidBody2D rb = new RigidBody2D();
                        rb.setBodyType(BodyType.STATIC);
                        object.addComponent(rb);
                        Box2DCollider b2d = new Box2DCollider();
                        b2d.setHalfSize(new Vector2f(0.25f,0.25f));
                        object.addComponent(b2d);
                        object.addComponent(new Ground());
                        if (i ==12) {
                            object.addComponent(new BreakableBrick());
                        }
                        //Attach to mouse cursor
                        levelEditorComponents.getComponent(MouseControls.class).pickupObject(object);
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
            if (ImGui.beginTabItem("Decoration Blocks")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;

                //Generate available tiles
                for (int i = 34; i < 61; i++) {

                    if (i >= 35 && i < 38) continue;
                    if (i >= 42 && i < 45) continue;

                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    //Push Custom ID to sprite, since without this they all share the spritesheet id
                    ImGui.pushID(i);

                    if (ImGui.imageButton(id, spriteWidth, spriteHeight,
                            texCoords[2].x, texCoords[0].y,
                            texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f); //old (sprite, spriteWidth, spriteHeight)
                        //Attach to mouse cursor
                        levelEditorComponents.getComponent(MouseControls.class).pickupObject(object);
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
            if (ImGui.beginTabItem("Prefabs")) {

                //SpriteSheets used
                Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
                Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
                Spritesheet pipes = AssetPool.getSpriteSheet("assets/images/spritesheets/pipes.png");
                Spritesheet turtle = AssetPool.getSpriteSheet("assets/images/turtle.png");
                Spritesheet icons = AssetPool.getSpriteSheet("assets/images/spritesheets/icons.png");
                Spritesheet deathBlock = AssetPool.getSpriteSheet("assets/images/blendImage1.png");


                Map<String, Sprite> prefabs = new HashMap<>();
                //Add prefabs
                prefabs.put("player", playerSprites.getSprite(0));
                prefabs.put("questionBlock", items.getSprite(0));
                prefabs.put("goomba", playerSprites.getSprite(14));
                prefabs.put("pipeDown", pipes.getSprite(0));
                prefabs.put("pipeUp", pipes.getSprite(1));
                prefabs.put("pipeRight", pipes.getSprite(2));
                prefabs.put("pipeLeft", pipes.getSprite(3));
                prefabs.put("turtle", turtle.getSprite(0));
                prefabs.put("flagPole", items.getSprite(33));
                prefabs.put("flagTop", items.getSprite(6));
                prefabs.put("deathBlock", deathBlock.getSprite(0));

                int uid = 0;

                for (String prefab : prefabs.keySet()) {
                    createPrefab(prefabs.get(prefab), uid, prefab);
                    uid++;
                    ImGui.sameLine();
                }

                ImGui.sameLine();

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = AssetPool.getAllSounds();

                ImVec2 windowSize= new ImVec2(ImGui.getWindowPos());
                for (Sound sound : sounds) {
                    File tmp = new File(sound.getFilepath());
                    if (ImGui.button(tmp.getName())) {
                        if (!sound.isPlaying()) {
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }
                    if (ImGui.getContentRegionAvailX() > 100) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }
        ImGui.end();
    }

    @Override
    public GameObject getSceneComponentObject() {
        return this.levelEditorComponents;
    }

    private void createPrefab(Sprite sprite, int uid, String prefab) {

        float spriteWidth = 64;
        float spriteHeight = 64;

        int id = sprite.getTexId();
        Vector2f[] texCoords = sprite.getTexCoords();

        ImGui.pushID(uid);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
            GameObject object = getPrefabObject(prefab);

            //Attach to mouse cursor
            levelEditorComponents.getComponent(MouseControls.class).pickupObject(object);
        }
        ImGui.popID();
    }

    private GameObject getPrefabObject(String prefab) {
        switch (prefab) {
            case "player":
                return Prefabs.generatePlayer();
            case "questionBlock":
                return Prefabs.generateQuestionBlock();
            case "goomba":
                return Prefabs.generateGoomba();
            case "pipeLeft":
                return Prefabs.generatePipe(Direction.Left);
            case "pipeRight":
                return Prefabs.generatePipe(Direction.Right);
            case "pipeUp":
                return Prefabs.generatePipe(Direction.Up);
            case "pipeDown":
                return Prefabs.generatePipe(Direction.Down);
            case "turtle":
                return Prefabs.generateTurtle();
            case "flagPole":
                return Prefabs.generateFlagPole();
            case "flagTop":
                return Prefabs.generateFlagTop();
            case "deathBlock":
                return Prefabs.generateDeathBlock();
        }
        assert false: "No such prefab as "+ prefab;
        GameObject broken = new GameObject("broken");
        broken.addComponent(new SpriteRenderer());
        return broken;
    }


}
