package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import exceptions.ItemTooHeavyException;

public class MailPool implements IMailPool {

    protected class Item {
        int priority;
        int destination;
        MailItem mailItem;
        // Use stable sort to keep arrival time relative positions

        public Item(MailItem mailItem) {
            priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
            destination = mailItem.getDestFloor();
            this.mailItem = mailItem;
        }
    }

    public class ItemComparator implements Comparator<Item> {
        @Override
        public int compare(Item i1, Item i2) {
            int order = 0;
            if (i1.priority < i2.priority) {
                order = 1;
            } else if (i1.priority > i2.priority) {
                order = -1;
            } else if (i1.destination < i2.destination) {
                order = 1;
            } else if (i1.destination > i2.destination) {
                order = -1;
            }
            return order;
        }
    }

    protected LinkedList<Item> pool;
    protected LinkedList<Robot> robots;
    protected IdistributeSystem distributeSystem;

    public MailPool() {
        // Start empty
        pool = new LinkedList<Item>();
        robots = new LinkedList<Robot>();
        distributeSystem = new SimpleDistributeSystem(pool, robots);
    }

//    @Override
    public void addToPool(MailItem mailItem) {
        Item item = new Item(mailItem);
        pool.add(item);
        pool.sort(new ItemComparator());
    }

    @Override
    public void step() throws ItemTooHeavyException {

        distributeSystem.distribute();
    }

//    @Override
    public void registerWaiting(Robot robot) { // assumes won't be there already
        robots.add(robot);
    }

}
