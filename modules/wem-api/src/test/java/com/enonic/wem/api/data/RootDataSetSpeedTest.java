package com.enonic.wem.api.data;


import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Stopwatch;


public class RootDataSetSpeedTest
{
    private static final int HUNDRED_THOUSAND = 100000;

    @Test
    @Ignore
    public void adding_one_million_properties_with_same_name_and_value_to_root()
    {
        Stopwatch stopwatch = Stopwatch.createUnstarted();

        RootDataSet tree = new RootDataSet();
        Value myValue = Value.newString( "myValue" );

        stopwatch.start();
        for ( int i = 0; i < HUNDRED_THOUSAND; i++ )
        {
            tree.addProperty( "myProp", myValue );
        }
        stopwatch.stop();

        System.out.println( "adding_one_million_properties_with_same_name_and_value_to_root: " + stopwatch.toString() );
    }

    @Test
    @Ignore
    public void adding_one_million_properties_with_same_name_and_different_value_to_root()
    {
        Stopwatch stopwatch = Stopwatch.createUnstarted();

        RootDataSet tree = new RootDataSet();

        stopwatch.start();
        for ( int i = 0; i < HUNDRED_THOUSAND; i++ )
        {
            tree.addProperty( "myProp", Value.newLong( i ) );
        }
        stopwatch.stop();

        System.out.println( "adding_one_million_properties_with_same_name_and_different_value_to_root: " + stopwatch.toString() );
    }
}
