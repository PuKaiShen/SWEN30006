package strategies;

import automail.MailItem;
import automail.Robot;
import exceptions.ItemTooHeavyException;

import java.util.LinkedList;
import java.util.ListIterator;


public class WeightDistributeSystem implements IdistributeSystem {

    private LinkedList<WeightDistributeMailPool.Item> pool;
    private LinkedList<Robot> robots;

    enum Weight{LIGHT, HEAVY, SUPER_HEAVY}


    public WeightDistributeSystem(LinkedList<WeightDistributeMailPool.Item> pool, LinkedList<Robot> robots) {
        this.pool=pool;
        this.robots=robots;
    }


    @Override
    public void distribute() {
        ListIterator<Robot> i = robots.listIterator();
        ListIterator<WeightDistributeMailPool.Item> j = pool.listIterator();
        while (i.hasNext()){
//            System.out.println("here2");
            i.next();
            WeightDistributeMailPool.Item item = null;
            if (j.hasNext()){
                i.previous();
//                System.out.println("here3");
                Weight weight = isHeavy(j);
                switch (weight){
                    case LIGHT:
//                        System.out.println("here6");
                        try{
                            WeightDistributeHelper.LoadLight(i, j);
                        }catch (ItemTooHeavyException e){
                            e.printStackTrace();
                        }
                        break;
                    case HEAVY:
//                        System.out.println("here7");
                        try {
                            WeightDistributeHelper.LoadHeavy(i, j, Weight.HEAVY);
                        } catch (ItemTooHeavyException e) {
                            e.printStackTrace();
                        }
                        break;
                    case SUPER_HEAVY:
//                        System.out.println("here8");
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

    protected static Weight isHeavy(ListIterator<WeightDistributeMailPool.Item> j){
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

    public static void LoadLight(ListIterator<Robot> i, ListIterator<WeightDistributeMailPool.Item> j) throws ItemTooHeavyException {
        // loading light item;
        Robot robot = i.next();
        assert (robot.isEmpty());
        WeightDistributeSystem.Weight weight = null;

        robot.addToHand(j.next().mailItem);
        j.remove();
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
        robot.dispatch();
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


        WeightDistributeSystem.Weight weight = null;

        MailItem mailItem = j.next().mailItem;
        // team Robot
        for (Robot robot : robots) {
            addToRobotHand(robot, mailItem, ItemWeight);
        }
        j.remove();

        for (Robot robot : robots){
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

        for (Robot robot : robots) {
            robot.teamDispatch();
            i.remove();
            if (i.hasPrevious()){
                i.previous();
            }
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
