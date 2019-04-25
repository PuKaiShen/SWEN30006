package strategies;

import automail.MailItem;

import automail.Robot;
import exceptions.ItemTooHeavyException;



/**
 * An distribution pool that used to distributing item to pools according to the item's weight.
 * The behavior of this mail pool is pretty similar to the Basic Mail pool.So, just simply Inheritance, if encounter
 * a case which is very different from the mailpool, you can choose to implement the interface.
 * */
public class WeightDistributeMailPool extends MailPool {

//    private IdistributeSystem distributeSystem;


    public WeightDistributeMailPool(IdistributeSystem distributeSystem){
        super();
        this.distributeSystem = distributeSystem;
    }

    public WeightDistributeMailPool() {
        super();
        distributeSystem = new WeightDistributeSystem(pool, robots);
    }

    @Override
    public void step() throws ItemTooHeavyException {
        distributeSystem.distribute();
    }
}