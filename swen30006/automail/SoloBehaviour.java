package automail;

public class SoloBehaviour implements IBehaviour {

    private Robot.Info robotInfo;

    public SoloBehaviour(Robot.Info robotInfo) {
        this.robotInfo = robotInfo;
        this.robotInfo.current_behaviour = Robot.RobotBehaviour.SOLO;
    }

    @Override
    public void moveTowards(int destination) {
        if (robotInfo.current_floor < destination){
            robotInfo.current_floor++;
        }else {
            robotInfo.current_floor--;
        }
    }
}
