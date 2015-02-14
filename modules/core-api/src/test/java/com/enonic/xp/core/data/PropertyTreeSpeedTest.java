package com.enonic.xp.core.data;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.core.data.PropertySet;
import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.data.Value;

@Ignore
public class PropertyTreeSpeedTest
{
    private static final int ONE_MILLION = 1000000;

    @Test
    public void adding_one_million_properties_with_same_name_and_value_to_root()
    {
        Stopwatch stopwatch = Stopwatch.createUnstarted();

        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        Value myValue = Value.newString( "myValue" );

        stopwatch.start();
        for ( int i = 0; i < ONE_MILLION; i++ )
        {
            tree.addProperty( "myProp", myValue );
        }
        stopwatch.stop();

        System.out.println( "adding_one_million_properties_with_same_name_and_value_to_root: " + stopwatch.toString() );
    }

    @Test
    public void adding_one_million_properties_with_same_name_and_different_value_to_root()
    {
        Stopwatch stopwatch = Stopwatch.createUnstarted();

        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        stopwatch.start();
        for ( int i = 0; i < ONE_MILLION; i++ )
        {
            tree.addProperty( "myProp", Value.newLong( (long) i ) );
        }
        stopwatch.stop();

        System.out.println( "adding_one_million_properties_with_same_name_and_different_value_to_root: " + stopwatch.toString() );
    }

    @Test
    public void creating_tree_with_10_branches_with_width_of_10_and_depth_of_5()
    {
        Stopwatch stopwatch = Stopwatch.createUnstarted();

        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        stopwatch.start();
        for ( int i = 0; i < 10; i++ )
        {
            PropertySet set = tree.newSet();
            tree.addProperty( "myProp", Value.newData( set ) );
            generateBranch( set, 10, 5 );
        }
        stopwatch.stop();

        System.out.println( "creating_tree_with_10_branches_with_width_of_10_and_depth_of_4_and_10_leaf_nodes: " + stopwatch.toString() );
        System.out.println( "number of properties: " + tree.getTotalSize() );
    }

    private void generateBranch( PropertySet parent, int numberOfChildNodes, int depth )
    {
        if ( depth > 0 )
        {
            for ( int i = 0; i < numberOfChildNodes; i++ )
            {
                PropertySet set = parent.newSet();
                parent.addProperty( "myProp", Value.newData( set ) );
                generateBranch( set, numberOfChildNodes, depth - 1 );

            }
        }
    }

}
