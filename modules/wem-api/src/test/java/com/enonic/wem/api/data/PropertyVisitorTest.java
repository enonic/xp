package com.enonic.wem.api.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        contentData.add( Property.newString( "myText", "abc" ) );
        contentData.add( Property.newLocalDate( "myDate", LocalDate.now() ) );

        DataSet mySet = DataSet.create().name( "mySet" ).build();
        mySet.add( Property.newString( "myText", "abc" ) );
        mySet.add( Property.newLocalDate( "myDate", LocalDate.now() ) );
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
        propertyVisitor.restrictType( ValueTypes.STRING );

        ContentData contentData = new ContentData();
        contentData.add( Property.newString( "myText", "abc" ) );
        contentData.add( Property.newLocalDate( "myDate", LocalDate.now() ) );

        DataSet mySet = DataSet.create().name( "mySet" ).build();
        mySet.add( Property.newString( "myText", "abc" ) );
        mySet.add( Property.newLocalDate( "myDate", LocalDate.now() ) );
        contentData.add( mySet );

        propertyVisitor.traverse( contentData );

        assertEquals( 2, hits.size() );
        assertEquals( "myText", hits.get( 0 ).getPath().toString() );
        assertEquals( "mySet.myText", hits.get( 1 ).getPath().toString() );
    }
}
