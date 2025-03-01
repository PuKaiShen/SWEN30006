package strategies;

import automail.MailItem;
import automail.Robot;
import exceptions.ItemTooHeavyException;

import java.util.LinkedList;
import java.util.ListIterator;


public class WeightDistributeSystem implements IdistributeSystem {

    enum Weight{LIGHT, HEAVY, SUPER_HEAVY}


    @Override
    public void distribute(LinkedList<Robot> robots, LinkedList<MailPool.Item> pool) {
        ListIterator<Robot> i = robots.listIterator();
        ListIterator<MailPool.Item> j = pool.listIterator();
        while (i.hasNext()){
            i.next();
//            WeightDistributeMailPool.Item item = null;
            if (j.hasNext()){
                i.previous();
                Weight weight = isHeavy(j);
                switch (weight){
                    case LIGHT:
                        try{
                            WeightDistributeHelper.LoadLight(i, j);
                        }catch (ItemTooHeavyException e){
                            e.printStackTrace();
                        }
                        break;
                    case HEAVY:
                        try {
                            WeightDistributeHelper.LoadHeavy(i, j, Weight.HEAVY);
                        } catch (ItemTooHeavyException e) {
                            e.printStackTrace();
                        }
                        break;
                    case SUPER_HEAVY:
                        try {
                            WeightDistributeHelper.LoadHeavy(i, j, Weight.SUPER_HEAVY);
                        } catch (ItemTooHeavyException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void dispatch(Robot robot) {
        WeightDistributeHelper.Dispatch(robot);
    }

    static Weight isHeavy(ListIterator<MailPool.Item> j){
        MailItem mailItem = j.next().mailItem;
        j.previous();// go back, so the next pointer can point to the true item
        if (mailItem.getWeight()<=Robot.INDIVIDUAL_MAX_WEIGHT){
            return Weight.LIGHT;
        }else if (mailItem.getWeight()<=Robot.PAIR_MAX_WEIGHT){
            return Weight.HEAVY;
        }else {
            return Weight.SUPER_HEAVY;
        }
    }
}

class WeightDistributeHelper {

    public static void LoadLight(ListIterator<Robot> i, ListIterator<MailPool.Item> j) throws ItemTooHeavyException {
        // loading light item;
        Robot robot = i.next();
        assert (robot.isEmpty());
//        WeightDistributeSystem.Weight weight = null;

        robot.addToHand(j.next().mailItem);
        j.remove();

        addToTube(j, robot);
//        while (j.hasNext()){
//            weight = WeightDistributeSystem.isHeavy(j);
//            if (weight.equals(WeightDistributeSystem.Weight.LIGHT)){
//                robot.addToTube(j.next().mailItem);
//                j.remove();
//                break;
//            }else {
//                j.next();
//            }
//        }
        Dispatch(robot);
        i.remove();
    }

    public static void LoadHeavy(ListIterator<Robot> i,
                                 ListIterator<MailPool.Item> j,
                                 WeightDistributeSystem.Weight ItemWeight) throws ItemTooHeavyException {
        Robot[] robots = null;
        switch (ItemWeight){
            case HEAVY:
                robots = new Robot[]{null, null};
                break;
            case SUPER_HEAVY:
                robots = new Robot[]{null, null, null};
                break;
        }

        assert (robots!=null);
        for (int k=0;k<robots.length;k++ ){// if not enough robots to carry the item, just leave it and return
            if (i.hasNext()) robots[k] = i.next(); else return;
            assert (robots[k].isEmpty());
        }


//        WeightDistributeSystem.Weight weight = null;

        MailItem mailItem = j.next().mailItem;
        // team Robot
        for (Robot robot : robots) {
            addToRobotHand(robot, mailItem, ItemWeight);
        }
        j.remove();

        for (Robot robot : robots){
            addToTube(j, robot);
//            while (j.hasNext()){
//                weight = WeightDistributeSystem.isHeavy(j);
//                if (weight.equals(WeightDistributeSystem.Weight.LIGHT)){
//                    robot.addToTube(j.next().mailItem);
//                    j.remove();
//                    break;
//                }else {
//                    j.next();
//                }
//            }
        }

        for (Robot robot : robots) {
            Dispatch(robot);
            i.remove();
            if (i.hasPrevious()){
                i.previous();
            }
        }
    }

    private static void addToTube(ListIterator<MailPool.Item> j, Robot robot) throws ItemTooHeavyException {

        WeightDistributeSystem.Weight weight = null;

        while (j.hasNext()){
            weight = WeightDistributeSystem.isHeavy(j);
            if (weight.equals(WeightDistributeSystem.Weight.LIGHT)){
                robot.addToTube(j.next().mailItem);
                j.remove();
                break;
            }else {
                j.next();
            }
        }
    }

    public static void Dispatch(Robot robot){
        robot.setReceivedDispatch(true);
        if (robot.getDeliveryItem().getWeight()>Robot.INDIVIDUAL_MAX_WEIGHT){
            robot.changeBehaviour(Robot.RobotBehaviour.TEAM);
        }
    }


    private static void addToRobotHand(Robot robot, MailItem mailItem, WeightDistributeSystem.Weight weight) throws ItemTooHeavyException{
        if (mailItem.getWeight()>Robot.INDIVIDUAL_MAX_WEIGHT && weight.equals(WeightDistributeSystem.Weight.LIGHT)){
            throw new ItemTooHeavyException();
        }else if (mailItem.getWeight()>Robot.PAIR_MAX_WEIGHT && weight.equals(WeightDistributeSystem.Weight.HEAVY)){
            throw new ItemTooHeavyException();
        }else if (mailItem.getWeight()>Robot.TRIPLE_MAX_WEIGHT && weight.equals(WeightDistributeSystem.Weight.SUPER_HEAVY)){
            throw new ItemTooHeavyException();
        }else {
            robot.addToHand(mailItem, true);
        }
    }
}
