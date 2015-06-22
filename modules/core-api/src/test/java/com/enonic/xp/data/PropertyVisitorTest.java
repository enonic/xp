package com.enonic.xp.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class PropertyVisitorTest
{
    @Test
    public void traverse_visitPropertiesWithSet()
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
        propertyVisitor.visitPropertiesWithSet( true );

        PropertyTree propertyTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet setA = propertyTree.addSet( "a" );
        PropertySet setB = setA.addSet( "b" );
        setB.addSet( "c" );

        // exercise
        propertyVisitor.traverse( propertyTree );

        // verify
        assertEquals( 3, hits.size() );
        assertEquals( "a", hits.get( 0 ).getPath().toString() );
        assertEquals( "a.b", hits.get( 1 ).getPath().toString() );
        assertEquals( "a.b.c", hits.get( 2 ).getPath().toString() );
    }

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

        PropertyTree propertyTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        propertyTree.addString( "myText", "abc" );
        propertyTree.addLocalDate( "myDate", LocalDate.now() );

        PropertySet mySet = propertyTree.addSet( "mySet" );
        mySet.addString( "myText", "abc" );
        mySet.addLocalDate( "myDate", LocalDate.now() );

        // exercise
        propertyVisitor.traverse( propertyTree );

        // verify
        assertEquals( 4, hits.size() );
        assertEquals( "myText", hits.get( 0 ).getPath().toString() );
        assertEquals( "myDate", hits.get( 1 ).getPath().toString() );
        assertEquals( "mySet.myText", hits.get( 2 ).getPath().toString() );
        assertEquals( "mySet.myDate", hits.get( 3 ).getPath().toString() );
    }

    @Test
    public void traverse_with_restriction_on_ValueType()
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
        propertyVisitor.restrictType( ValueTypes.LOCAL_DATE );

        PropertyTree propertyTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        propertyTree.addString( "myText", "abc" );
        propertyTree.addLocalDate( "myDate", LocalDate.now() );

        PropertySet mySet = propertyTree.addSet( "mySet" );
        mySet.addString( "myText", "abc" );
        mySet.addLocalDate( "myDate", LocalDate.now() );

        // exercise
        propertyVisitor.traverse( propertyTree );

        // verify
        assertEquals( 2, hits.size() );
        assertEquals( "myDate", hits.get( 0 ).getPath().toString() );
        assertEquals( "mySet.myDate", hits.get( 1 ).getPath().toString() );
    }

    @Test
    public void traverse_with_restriction_on_ValueType_Reference()
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
        propertyVisitor.restrictType( ValueTypes.REFERENCE );

        PropertyTree propertyTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        propertyTree.addString( "myText", "abc" );
        propertyTree.addLocalDate( "myDate", LocalDate.now() );
        propertyTree.addReference( "myRef", Reference.from( "nodeId-1" ) );

        PropertySet mySet = propertyTree.addSet( "mySet" );
        mySet.addString( "myText", "abc" );
        mySet.addLocalDate( "myDate", LocalDate.now() );
        mySet.addReference( "myRef", Reference.from( "nodeId-2" ) );

        // exercise
        propertyVisitor.traverse( propertyTree );

        // verify
        assertEquals( 2, hits.size() );
        assertEquals( "myRef", hits.get( 0 ).getPath().toString() );
        assertEquals( "mySet.myRef", hits.get( 1 ).getPath().toString() );
    }


    @Test
    public void traverse_with_restriction_on_ValueType_Link()
    {
        final List<Property> hits = new ArrayList<>();

        final PropertyVisitor propertyVisitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property link )
            {
                hits.add( link );
            }
        };
        propertyVisitor.restrictType( ValueTypes.LINK );

        PropertyTree propertyTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        propertyTree.addString( "myText", "abc" );
        propertyTree.addLocalDate( "myDate", LocalDate.now() );
        propertyTree.addLink( "myLink", Link.from( "/nodeId-1" ) );

        PropertySet mySet = propertyTree.addSet( "mySet" );
        mySet.addString( "myText", "abc" );
        mySet.addLocalDate( "myDate", LocalDate.now() );
        mySet.addLink( "myLink", Link.from( "/nodeId-2" ) );

        // exercise
        propertyVisitor.traverse( propertyTree );

        // verify
        assertEquals( 2, hits.size() );
        assertEquals( "myLink", hits.get( 0 ).getPath().toString() );
        assertEquals( "mySet.myLink", hits.get( 1 ).getPath().toString() );
    }

}
