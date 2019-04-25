package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;

import java.util.Map;
import java.util.TreeMap;

/**
 * The robot delivers mail!
 */
public class Robot {

    static public final int INDIVIDUAL_MAX_WEIGHT = 2000;
    static public final int PAIR_MAX_WEIGHT = 2600;
    static public final int TRIPLE_MAX_WEIGHT = 3000;

    public class Info{
        int current_floor;
        public RobotState current_state;
        public RobotBehaviour current_behaviour;

        public Info(int current_floor, RobotState robotState, RobotBehaviour robotBehaviour) {
            this.current_floor = current_floor;
            this.current_state = robotState;
            this.current_behaviour = robotBehaviour;
        }
    }

    IMailDelivery delivery;
    protected final String id;

    /**
     * Possible states the robot can be in
     */
    public enum RobotState {DELIVERING, WAITING, RETURNING}
    public enum RobotBehaviour {TEAM, SOLO}

    private Info info;

    private int destination_floor;
    private IMailPool mailPool;
    private boolean receivedDispatch;

    private MailItem deliveryItem = null;
    private MailItem tube = null;

    public RobotBehaviour getBehaviour() {
        return info.current_behaviour;
    }

    private void changeBehaviour(RobotBehaviour robotBehaviour){
        switch (robotBehaviour){
            case SOLO:
                behaviour = new SoloBehaviour(info);
                break;
            case TEAM:
                behaviour = new TeamBehaviour(info);
                break;
        }
    }

    /** added new attribute **/

    /*version 2.0*/
    private IBehaviour behaviour;
    /*end of version 2.0*/


    private int deliveryCounter;

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     *
     * @param delivery  governs the final delivery
     * @param mailPool  is the source of mail items
     */
    public Robot(IMailDelivery delivery, IMailPool mailPool) {
        id = "R" + hashCode();
        // current_state = RobotState.WAITING;
        // current_state = RobotState.RETURNING;

        info = new Info(Building.MAILROOM_LOCATION, RobotState.RETURNING, RobotBehaviour.SOLO);
        /*default mode is solo*/
        behaviour = new SoloBehaviour(info);

        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
    }

    public void dispatch() {
        receivedDispatch = true;
    }

    public void teamDispatch(){
        dispatch();
        changeBehaviour(RobotBehaviour.TEAM);
    }
    /**
     * This is called on every time step
     *
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void step() throws ExcessiveDeliveryException {
        switch (info.current_state) {
            /** This state is triggered when the robot is returning to the mailroom after a delivery */
            case RETURNING:
                /** If its current position is at the mailroom, then the robot should change state */
                if (info.current_floor == Building.MAILROOM_LOCATION) {
                    if (tube != null) {
                        mailPool.addToPool(tube);
                        System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), tube.toString());
                        tube = null;
                    }
                    /** Tell the sorter the robot is ready */
                    mailPool.registerWaiting(this);
                    changeState(RobotState.WAITING);
                } else {
                    /** If the robot is not at the mailroom floor yet, then move towards it! */
                    behaviour.moveTowards(Building.MAILROOM_LOCATION);
                    break;
                }
            case WAITING:
                /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if (!isEmpty() && receivedDispatch) {
                    receivedDispatch = false;
                    deliveryCounter = 0; // reset delivery counter
                    setRoute();
                    changeState(RobotState.DELIVERING);
                }
                break;
            case DELIVERING:
                if (info.current_floor == destination_floor) { // If already here drop off either way
                    /** Delivery complete, report this to the simulator! */
                    delivery.deliver(deliveryItem);
                    deliveryItem = null;
                    deliveryCounter++;
                    if (deliveryCounter > 2) {  // Implies a simulation bug
                        throw new ExcessiveDeliveryException();
                    }
                    /** Check if want to return, i.e. if there is no item in the tube*/
                    if (tube == null) {
                        changeState(RobotState.RETURNING);
                    } else {
                        /** If there is another item, set the robot's route to the location to deliver the item */
                        deliveryItem = tube;
                        tube = null;
                        setRoute();
                        changeState(RobotState.DELIVERING);
                    }
                    changeBehaviour(RobotBehaviour.SOLO);
                } else {
                    /** The robot is not at the destination yet, move towards it! */
                    behaviour.moveTowards(destination_floor);
                }
                break;
        }
    }

    /**
     * Sets the route for the robot
     */
    private void setRoute() {
        /** Set the destination floor */
        destination_floor = deliveryItem.getDestFloor();
    }

    private String getIdTube() {
        return String.format("%s(%1d)", id, (tube == null ? 0 : 1));
    }

    /**
     * Prints out the change in state
     *
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState) {
        assert (!(deliveryItem == null && tube != null));
        if (info.current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), info.current_state, nextState);
        }
        info.current_state = nextState;
        if (nextState == RobotState.DELIVERING) {
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
        }
    }

    public MailItem getTube() {
        return tube;
    }

    static private int count = 0;
    static private Map<Integer, Integer> hashMap = new TreeMap<Integer, Integer>();

    @Override
    public int hashCode() {
        Integer hash0 = super.hashCode();
        Integer hash = hashMap.get(hash0);
        if (hash == null) {
            hash = count++;
            hashMap.put(hash0, hash);
        }
        return hash;
    }

    public boolean isEmpty() {
        return (deliveryItem == null && tube == null);
    }

    public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
        assert (deliveryItem == null);
        if (mailItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
        deliveryItem = mailItem;
    }

    /*move the guard to the distributor, instead of robot*/
    public void addToHand(MailItem mailItem, boolean passWeightCheck) {
        assert (deliveryItem == null);
        deliveryItem = mailItem;
    }

    public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
        assert (tube == null);
        tube = mailItem;
        if (tube.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
    }
}
