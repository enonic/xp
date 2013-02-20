package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.data.type.DataTypes;

import static junit.framework.Assert.assertEquals;

public class DataVisitorTest
{

    @Test
    public void traverse()
    {
        final List<Data> hits = new ArrayList<>();

        final DataVisitor dataVisitor = new DataVisitor()
        {
            @Override
            public void visit( final Data reference )
            {
                hits.add( reference );
            }
        };
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.add( Data.newData().name( "myText" ).type( DataTypes.TEXT ).value( "abc" ).build() );
        rootDataSet.add( Data.newData().name( "myDate" ).type( DataTypes.DATE ).value( DateMidnight.now() ).build() );

        DataSet mySet = DataSet.newDataSet().name( "mySet" ).build();
        mySet.add( Data.newData().name( "myText" ).type( DataTypes.TEXT ).value( "abc" ).build() );
        mySet.add( Data.newData().name( "myDate" ).type( DataTypes.DATE ).value( DateMidnight.now() ).build() );
        rootDataSet.add( mySet );

        dataVisitor.traverse( rootDataSet );

        assertEquals( 4, hits.size() );
        assertEquals( "myText", hits.get( 0 ).getPath().toString() );
        assertEquals( "myDate", hits.get( 1 ).getPath().toString() );
        assertEquals( "mySet.myText", hits.get( 2 ).getPath().toString() );
        assertEquals( "mySet.myDate", hits.get( 3 ).getPath().toString() );
    }

    @Test
    public void traverse_with_restriction_on_DataType()
    {
        final List<Data> hits = new ArrayList<>();

        final DataVisitor dataVisitor = new DataVisitor()
        {
            @Override
            public void visit( final Data reference )
            {
                hits.add( reference );
            }
        };
        dataVisitor.restrictType( DataTypes.TEXT );

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.add( Data.newData().name( "myText" ).type( DataTypes.TEXT ).value( "abc" ).build() );
        rootDataSet.add( Data.newData().name( "myDate" ).type( DataTypes.DATE ).value( DateMidnight.now() ).build() );

        DataSet mySet = DataSet.newDataSet().name( "mySet" ).build();
        mySet.add( Data.newData().name( "myText" ).type( DataTypes.TEXT ).value( "abc" ).build() );
        mySet.add( Data.newData().name( "myDate" ).type( DataTypes.DATE ).value( DateMidnight.now() ).build() );
        rootDataSet.add( mySet );

        dataVisitor.traverse( rootDataSet );

        assertEquals( 2, hits.size() );
        assertEquals( "myText", hits.get( 0 ).getPath().toString() );
        assertEquals( "mySet.myText", hits.get( 1 ).getPath().toString() );
    }
}
