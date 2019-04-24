package automail;

public class TeamBehaviour implements IBehaviour {

    private Robot.Info robotInfo;
    private int stepCount;

    public TeamBehaviour(Robot.Info robotInfo) {
        this.robotInfo = robotInfo;
        stepCount=0;
    }

    @Override
    public void moveTowards(int destination) {

        if (robotInfo.current_floor < destination){
            stepCount++;
            if (stepCount>=3){
                robotInfo.current_floor++;
                stepCount=0;
            }
        }else {
            if (stepCount>=3){
                robotInfo.current_floor--;
                stepCount=0;
            }
        }
    }
}
