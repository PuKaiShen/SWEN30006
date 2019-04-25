package automail;

public interface IBehaviour {

    /**
     * Generic function that moves the robot towards the destination (moved from the robot class)
     *
     * @param destination the floor towards which the robot is moving
     */
    void moveTowards(int destination);

}
