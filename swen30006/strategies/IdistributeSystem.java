package strategies;

import automail.MailItem;
import automail.Robot;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * An distribution system that used to help distributing item to robots.
 * */
public interface IdistributeSystem {

    /*distribute mail/mails to robot/robots*/
    void distribute();
}
