package strategies;

import automail.Robot;

public interface IdistributeSystem {

    /*do the job of distribution*/
    void distribute();

    /*dispatch the robot after distribute*/
    void dispatch(Robot robot);
}
