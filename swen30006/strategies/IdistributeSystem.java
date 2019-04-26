package strategies;

import automail.Robot;

import java.util.LinkedList;

public interface IdistributeSystem {

    /*do the job of distribution*/
    void distribute(LinkedList<Robot> robots, LinkedList<MailPool.Item> pool);

    /*dispatch the robot after distribute*/
    void dispatch(Robot robot);
}
