package components;

import engine.GameObject;
import engine.Prefabs;
import engine.Window;

public class QuestionBlock extends Block{

    private enum BlockType{
        Coin,
        PowerUp,
        Invincibility
    }

    public BlockType blockType = BlockType.Coin;

    @Override
    public void playerHit(PlayerController playerController) {
        switch(blockType){
            case Coin:
                doCoin(playerController);
                break;
            case PowerUp:
                doPowerUp(playerController);
                break;
            case Invincibility:
                doInvincibility(playerController);
                break;
        }

        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine != null){
            stateMachine.trigger("setInactive");
            this.setInactive();
        }

    }

    private void doInvincibility(PlayerController playerController) {
    }

    private void doPowerUp(PlayerController playerController) {
    }

    private void doCoin(PlayerController playerController) {
        GameObject coin = Prefabs.generateBlockCoin();
        coin.transform.position.set(this.gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(coin);
    }
}
