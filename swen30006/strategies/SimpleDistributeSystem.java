package strategies;

import automail.Robot;
import exceptions.ItemTooHeavyException;

import java.util.LinkedList;
import java.util.ListIterator;

public class SimpleDistributeSystem implements IdistributeSystem{

    @Override
    public void distribute(LinkedList<Robot> robots, LinkedList<MailPool.Item> pool) {
        try {
            ListIterator<Robot> i = robots.listIterator();
            while (i.hasNext())loadRobot(i, pool);
        } catch (ItemTooHeavyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatch(Robot robot) {
        robot.setReceivedDispatch(true);
    }

    private void loadRobot(ListIterator<Robot> i, LinkedList<MailPool.Item> pool) throws ItemTooHeavyException {
        Robot robot = i.next();
        assert (robot.isEmpty());
        // System.out.printf("P: %3d%n", pool.size());
        ListIterator<MailPool.Item> j = pool.listIterator();
        if (pool.size() > 0) {
            try {
                robot.addToHand(j.next().mailItem); // hand first as we want higher priority delivered first
                j.remove();
                if (pool.size() > 0) {
                    robot.addToTube(j.next().mailItem);
                    j.remove();
                }
                dispatch(robot); // send the robot off if it has any items to deliver
                i.remove();       // remove from mailPool queue
            } catch (ItemTooHeavyException e) {

            } catch (Exception e) {
                throw e;
            }
        }
    }
}
