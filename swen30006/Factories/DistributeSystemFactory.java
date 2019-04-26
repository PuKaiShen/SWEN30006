package Factories;

import strategies.IdistributeSystem;
import strategies.SimpleDistributeSystem;
import strategies.WeightDistributeSystem;

public class DistributeSystemFactory {

    public enum Distribution{
        SIMPLE,
        WEIGHT
    }

    private static DistributeSystemFactory instance = new DistributeSystemFactory();

    private DistributeSystemFactory(){}

    public static DistributeSystemFactory getInstance(){
        return instance;
    }

    public IdistributeSystem getDistributeSystem(Distribution distribution){

        if (distribution.equals(Distribution.WEIGHT)){
            return new WeightDistributeSystem();
        }else if(distribution.equals(Distribution.SIMPLE)){
            return new SimpleDistributeSystem();
        }

        return null;
    }

//    public SimpleDistributeSystem getSimpleDistributeSystem (){
//        return new SimpleDistributeSystem();
//    }
//
//    public WeightDistributeSystem getWeightDistributeSystem (){
//        return new WeightDistributeSystem();
//    }

}
