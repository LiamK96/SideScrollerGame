package components;

import util.AssetPool;

public class BreakableBrick extends Block {


    @Override
    public void playerHit(PlayerController playerController) {
        if (!playerController.isSmall()){
            AssetPool.getSound("assets/sounds/break_block.ogg").playWithOverlap();
            gameObject.destroy();
        }
    }
}
