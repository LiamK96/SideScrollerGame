package editor;

import engine.MouseListener;
import engine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;

public class GameViewWindow {

    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;

    public void imgui(){
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                    | ImGuiWindowFlags.MenuBar);

        //Play Stop menubar
        ImGui.beginMenuBar();

        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)){
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)){
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }

        ImGui.endMenuBar();



        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setWindowPos(windowPos.x,windowPos.y);

        //todo: find the reason for this offset
        int offsetY = 54;
        int offsetX = 10;
        //Set static variables
        leftX = windowPos.x + offsetX;
        bottomY = windowPos.y +offsetY;
        rightX = windowPos.x + windowSize.x + offsetX;
        topY = windowPos.y + windowSize.y + offsetY;

        int texID = Window.getFramebuffer().getTextureID();
        ImGui.image(texID, windowSize.x, windowSize.y, 0, 1,1,0);

        MouseListener.setGameViewportPos(new Vector2f(windowPos.x+offsetX,windowPos.y+offsetY));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x,windowSize.y));

        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport(){
        //           16/9 aspect ratio
        final float ASPECT_RATIO_X = 16.0f;
        final float ASPECT_RATIO_Y = 9.0f;
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectX = windowSize.x;
        float aspectY = (aspectX/ASPECT_RATIO_X) * ASPECT_RATIO_Y;
        if (aspectY> windowSize.y){
            aspectY = windowSize.y;
            aspectX = (aspectY/ASPECT_RATIO_Y) * ASPECT_RATIO_X;
        }

        return new ImVec2(aspectX,aspectY);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 gameScreenSize){
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float centerX = (windowSize.x/2.0f) - (gameScreenSize.x/2.0f);
        float centerY = (windowSize.y/2.0f) - (gameScreenSize.y/2.0f) - 30.0f;

        return new ImVec2(centerX + ImGui.getCursorPosX(),centerY + ImGui.getCursorPosY());
    }

    public boolean getWantCaptureMouse(){
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

}
