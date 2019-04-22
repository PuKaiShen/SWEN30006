package strategies;

import automail.Robot;
import exceptions.ItemTooHeavyException;

import java.util.Arrays;
import java.util.ListIterator;

public class NewMailPool extends MailPool {

    @Override
    protected void loadRobot(ListIterator<Robot> i) throws ItemTooHeavyException {

        int robot_used=0;
        Robot[] robots = {i.next(), null, null};
        assert (robots[0].isEmpty());
        ListIterator<Item> j = pool.listIterator();
        // adding item to hand
        if (pool.size() > 0) {
            Item new_item = j.next();
            if (new_item.mailItem.getWeight()<=Robot.INDIVIDUAL_MAX_WEIGHT){
                robots[0].addToHand(new_item.mailItem);
                robot_used=1;
                j.remove();
            }else if (new_item.mailItem.getWeight()<=Robot.PAIR_MAX_WEIGHT){
                if (i.hasNext()) robots[1] = i.next();else return;
                assert (robots[1].isEmpty());
                // add to two robots' hand
                robots[0].pairAddToHand(new_item.mailItem);
                robots[1].pairAddToHand(new_item.mailItem);
                robot_used=2;
                j.remove();
            }else if (new_item.mailItem.getWeight()<=Robot.TRIPLE_MAX_WEIGHT) {
                System.out.println("robots in pool : " + this.robots.size());
                if (i.hasNext()) robots[1] = i.next();else return;

                assert (robots[1].isEmpty());
                if (i.hasNext())robots[2] = i.next();else return;
                assert (robots[2].isEmpty());
                // add to three robots' hand
                robots[0].tripleAddToHand(new_item.mailItem);
                robots[1].tripleAddToHand(new_item.mailItem);
                robots[2].tripleAddToHand(new_item.mailItem);

                robot_used=3;
                j.remove();
            }else {
                throw new ItemTooHeavyException();
            }
            // adding on tube
            int k = 0;
            while (pool.size()>0 && k<robot_used){
                if (robots[k]!=null){
                    while ((new_item = j.next())!= null){
                        System.out.println("here2");
                        if (new_item.mailItem.getWeight()<=Robot.INDIVIDUAL_MAX_WEIGHT){
                            robots[k].addToTube(new_item.mailItem);
                            j.remove();
                            k++;
                            break;
                        }
                    }
                }  else {
                    break;
                }
            }
            // start dispatching
            k=0;
            while (k<robot_used){
                System.out.println("here3");
                if (robot_used==1){
                    robots[k].dispatch();
                    i.remove();
                    break;
                }else {
                    robots[k].teamDispatch();
                    i.remove();
                    if (i.hasPrevious()){
                        i.previous();
                    }
                }
                k++;
            }
        }

    }
}
