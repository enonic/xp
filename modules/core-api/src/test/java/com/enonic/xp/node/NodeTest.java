package com.enonic.xp.node;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.support.AbstractEqualsTest;

public class NodeTest
{

    @Test
    public void equals()
    {
        NodeName name1 = NodeName.from( "name1" );
        NodeName name2 = NodeName.from( "name2" );
        ChildOrder childOrder1 = ChildOrder.defaultOrder();
        ChildOrder childOrder2 = ChildOrder.manualOrder();
        PropertyTree data1 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        data1.addString( "some", "config" );

        PropertyTree data2 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        data2.addString( "other", "config" );

        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return Node.create().
                    name( name1 ).
                    data( data1 ).
                    childOrder( childOrder1 ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                Node notX1 = Node.create().
                    name( name2 ).
                    data( data1 ).
                    childOrder( childOrder1 ).
                    build();

                Node notX2 = Node.create().
                    name( name1 ).
                    data( data2 ).
                    childOrder( childOrder1 ).
                    build();

                Node notX3 = Node.create().
                    name( name1 ).
                    data( data1 ).
                    childOrder( childOrder2 ).
                    build();

                return new Object[]{notX1, notX2, notX3};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return Node.create().
                    name( name1 ).
                    data( data1 ).
                    childOrder( childOrder1 ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return Node.create().
                    name( name1 ).
                    data( data1 ).
                    childOrder( childOrder1 ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

}