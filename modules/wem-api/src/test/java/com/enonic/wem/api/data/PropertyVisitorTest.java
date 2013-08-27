package com.enonic.wem.api.data;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.type.ValueTypes;

import static junit.framework.Assert.assertEquals;

public class PropertyVisitorTest
{

    @Test
    public void traverse()
    {
        final List<Property> hits = new ArrayList<>();

        final PropertyVisitor propertyVisitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property reference )
            {
                hits.add( reference );
            }
        };
        ContentData contentData = new ContentData();
        contentData.add( Property.newProperty().name( "myText" ).type( ValueTypes.TEXT ).value( "abc" ).build() );
        contentData.add( Property.newProperty().name( "myDate" ).type( ValueTypes.DATE_MIDNIGHT ).value( DateMidnight.now() ).build() );

        DataSet mySet = DataSet.newDataSet().name( "mySet" ).build();
        mySet.add( Property.newProperty().name( "myText" ).type( ValueTypes.TEXT ).value( "abc" ).build() );
        mySet.add( Property.newProperty().name( "myDate" ).type( ValueTypes.DATE_MIDNIGHT ).value( DateMidnight.now() ).build() );
        contentData.add( mySet );

        propertyVisitor.traverse( contentData );

        assertEquals( 4, hits.size() );
        assertEquals( "myText", hits.get( 0 ).getPath().toString() );
        assertEquals( "myDate", hits.get( 1 ).getPath().toString() );
        assertEquals( "mySet.myText", hits.get( 2 ).getPath().toString() );
        assertEquals( "mySet.myDate", hits.get( 3 ).getPath().toString() );
    }

    @Test
    public void traverse_with_restriction_on_DataType()
    {
        final List<Property> hits = new ArrayList<>();

        final PropertyVisitor propertyVisitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property reference )
            {
                hits.add( reference );
            }
        };
        propertyVisitor.restrictType( ValueTypes.TEXT );

        ContentData contentData = new ContentData();
        contentData.add( Property.newProperty().name( "myText" ).type( ValueTypes.TEXT ).value( "abc" ).build() );
        contentData.add( Property.newProperty().name( "myDate" ).type( ValueTypes.DATE_MIDNIGHT ).value( DateMidnight.now() ).build() );

        DataSet mySet = DataSet.newDataSet().name( "mySet" ).build();
        mySet.add( Property.newProperty().name( "myText" ).type( ValueTypes.TEXT ).value( "abc" ).build() );
        mySet.add( Property.newProperty().name( "myDate" ).type( ValueTypes.DATE_MIDNIGHT ).value( DateMidnight.now() ).build() );
        contentData.add( mySet );

        propertyVisitor.traverse( contentData );

        assertEquals( 2, hits.size() );
        assertEquals( "myText", hits.get( 0 ).getPath().toString() );
        assertEquals( "mySet.myText", hits.get( 1 ).getPath().toString() );
    }
}
