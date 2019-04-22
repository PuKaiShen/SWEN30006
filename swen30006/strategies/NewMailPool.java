package strategies;

import automail.Robot;
import exceptions.ItemTooHeavyException;

import java.util.ListIterator;

public class NewMailPool extends MailPool {

    @Override
    protected void loadRobot(ListIterator<Robot> i) throws ItemTooHeavyException {

        Robot[] robots = {i.next(), null, null};
        assert (robots[0].isEmpty());
        ListIterator<Item> j = pool.listIterator();
        if (pool.size() > 0) {
            Item new_item = j.next();
            if (new_item.mailItem.getWeight()<=Robot.INDIVIDUAL_MAX_WEIGHT){
                robots[0].addToHand(new_item.mailItem);
                j.remove();
            }else if (new_item.mailItem.getWeight()<=Robot.PAIR_MAX_WEIGHT){
                robots[1] = i.next();
                assert (robots[1].isEmpty());
                // add to two robots' hand
                j.remove();
            }else if (new_item.mailItem.getWeight()<=Robot.TRIPLE_MAX_WEIGHT) {
                robots[1] = i.next();
                assert (robots[1].isEmpty());
                robots[2] = i.next();
                assert (robots[2].isEmpty());
                // add to three robots' hand
                j.remove();
            }else {
                throw new ItemTooHeavyException();
            }
            // tube item
            int k = 0;
            while (pool.size()>0 && k<3){
                while ((new_item = j.next())!= null){
                    if (new_item.mailItem.getWeight()<=Robot.INDIVIDUAL_MAX_WEIGHT){
                        robots[k].addToTube(new_item.mailItem);
                        j.remove();
                        k++;
                        break;
                    }
                }
            }



//            try {
//                robot.addToHand(j.next().mailItem); // hand first as we want higher priority delivered first
//                j.remove();
//                if (pool.size() > 0) {
//                    robot.addToTube(j.next().mailItem);
//                    j.remove();
//                }
//                robot.dispatch(); // send the robot off if it has any items to deliver
//                i.remove();       // remove from mailPool queue
//            }catch (Exception e) {
//                throw e;
//            }
        }

    }
}
