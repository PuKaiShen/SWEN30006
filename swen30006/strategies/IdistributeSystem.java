package strategies;

import automail.Robot;

public interface IdistributeSystem {

    /*do the job of distribution*/
    public void distribute();

    /*dispatch the robot after distribute*/
    public void dispatch(Robot robot);
}
