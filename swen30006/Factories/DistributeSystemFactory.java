package Factories;

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

    public SimpleDistributeSystem getSimpleDistributeSystem (){
        return new SimpleDistributeSystem();
    }

    public WeightDistributeSystem getWeightDistributeSystem (){
        return new WeightDistributeSystem();
    }

}
