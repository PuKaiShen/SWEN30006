package strategies;

import automail.IMailDelivery;
import automail.Robot;
import automail.SoloBehaviour;

public class Automail {

    public Robot[] robots;
    public IMailPool mailPool;

    public Automail(IMailPool mailPool, IMailDelivery delivery, int numRobots) {
        // Swap between simple provided strategies and your strategies here

        /** Initialize the MailPool */

        this.mailPool = mailPool;

        /** Initialize robots */
        robots = new Robot[numRobots];
        for (int i = 0; i < numRobots; i++) {
            // for now, default robot is in solo mode
            robots[i] = new Robot(delivery, mailPool);
        }
    }
}
