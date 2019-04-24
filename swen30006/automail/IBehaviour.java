package automail;

public interface IBehaviour {

    /**
     * Generic function that moves the robot towards the destination
     *
     * @param destination the floor towards which the robot is moving
     */
    void moveTowards(int destination);

}
