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

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        //Get Screen position for Viewport
        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();

        //Set static variables
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

        int texID = Window.getFramebuffer().getTextureID();
        ImGui.image(texID, windowSize.x, windowSize.y, 0, 1,1,0);

        MouseListener.setGameViewportPos(new Vector2f(topLeft.x,topLeft.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x,windowSize.y));

        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport(){
        //           16/9 aspect ratio
        final float ASPECT_RATIO_X = 16.0f;
        final float ASPECT_RATIO_Y = 9.0f;
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

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
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float centerX = (windowSize.x/2.0f) - (gameScreenSize.x/2.0f);
        float centerY = (windowSize.y/2.0f) - (gameScreenSize.y/2.0f);

        return new ImVec2(centerX + ImGui.getCursorPosX(),centerY + ImGui.getCursorPosY());
    }

    public boolean getWantCaptureMouse(){
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

}
