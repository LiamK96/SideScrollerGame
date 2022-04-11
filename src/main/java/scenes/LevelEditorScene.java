package scenes;

import components.*;
import engine.Camera;
import engine.GameObject;
import engine.Prefabs;
import engine.Transform;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private Spritesheet sprites;

    public GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()),0);

    public LevelEditorScene(){

    }

    @Override
    public void init(){
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());

        loadResources();
        this.camera = new Camera(new Vector2f());
        sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        System.out.println("init");
        if (levelLoaded){
            if (gameObjects.size() > 0) {
                this.activeGameObject = gameObjects.get(0);
            }
            return;
        }
    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16,16,81,0));
        AssetPool.getTexture("assets/images/blendImage1.png");

        for (GameObject go : gameObjects){
            if (go.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr.getTexture()!=null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }

    @Override
    public void update(float dt) {
        levelEditorStuff.update(dt);

        for (GameObject go : this.gameObjects){
            go.update(dt);
        }
    }

    @Override
    public void render(){
        this.renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Test window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;

        //Generate available tiles
        for (int i = 0; i < sprites.size(); i++){
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            //Push Custom ID to sprite, since without this they all share the spritesheet id
            ImGui.pushID(i);

            if (ImGui.imageButton(id,spriteWidth,spriteHeight,texCoords[2].x,texCoords[0].y,texCoords[0].x,texCoords[2].y)){
                GameObject object = Prefabs.generateSpriteObject(sprite,64, 64); //old (sprite, spriteWidth, spriteHeight)

                //Attach to mouse cursor
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
            }
            //Pop custom ID
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if (i+1 < sprites.size() && nextButtonX2 < windowX2){
                ImGui.sameLine();
            }
        }
        ImGui.end();
    }

}
