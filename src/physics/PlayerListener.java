package physics;

/**
 *
 * @author admin
 */
public interface PlayerListener {

    public void onPlayerStop();
    
    public void onPlayerFoward();
    public void onPlayerBackward();

    public void onPlayerRotateLeft();
    public void onPlayerRotateRight();

    public void onPlayerJumpStart();
    public void onPlayerJumpTop();
    public void onPlayerJumpEnd();

    public void onPlayerStrafe();
    
}
