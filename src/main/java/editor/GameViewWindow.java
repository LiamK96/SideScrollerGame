package editor;

import engine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class GameViewWindow {

    public static void imgui(){
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);
        int texID = Window.getFramebuffer().getTextureID();
        ImGui.image(texID, windowSize.x, windowSize.y, 0, 1,1,0);

        ImGui.end();
    }

    private static ImVec2 getLargestSizeForViewport(){
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

    private static ImVec2 getCenteredPositionForViewport(ImVec2 gameScreenSize){
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float centerX = (windowSize.x/2.0f) - (gameScreenSize.x/2.0f);
        float centerY = (windowSize.y/2.0f) - (gameScreenSize.y/2.0f);

        return new ImVec2(centerX + ImGui.getCursorPosX(),centerY + ImGui.getCursorPosY());
    }

}
