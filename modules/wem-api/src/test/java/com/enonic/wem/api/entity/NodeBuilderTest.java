package com.enonic.wem.api.entity;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static org.junit.Assert.*;

public class NodeBuilderTest
{
    @Test
    public void validate_given_no_parent_then_exception()
        throws Exception
    {

        try
        {
            Node.newNode().name( "myNode" ).build();
        }
        catch ( NullPointerException e )
        {
            assertEquals( "parent must be specified", e.getMessage() );
            return;
        }

        fail( "expected missing parent exception" );
    }

    @Test
    public void validate_given_no_name_then_exception()
        throws Exception
    {
        try
        {
            Node.newNode().parent( NodePath.ROOT ).build();
        }
        catch ( NullPointerException e )
        {
            assertEquals( "name must be specified", e.getMessage() );
            return;
        }

        fail( "expected missing parent exception" );
    }

    @Test
    public void build_given_no_properties_then_rootDataSet_not_null()
        throws Exception
    {
        final Node myNode = Node.newNode().name( "myNode" ).parent( NodePath.ROOT ).build();
        assertNotNull( myNode.rootDataSet() );
    }

    @Test
    public void build_property_given_String()
        throws Exception
    {

        final Node myNode = Node.newNode().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            property( "testPath", "testValue" ).
            build();

        final Property testProperty = myNode.property( "testPath" );

        assertNotNull( testProperty );
        assertTrue( testProperty.getValue() instanceof Value.String );
    }

    @Test
    public void build_property_given_Long()
        throws Exception
    {

        final Node myNode = Node.newNode().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            property( "testPath", 1L ).
            build();

        final Property testProperty = myNode.property( "testPath" );

        assertNotNull( testProperty );
        assertTrue( testProperty.getValue() instanceof Value.Long );
    }

    @Test
    public void build_property_given_DateTime()
        throws Exception
    {

        final Node myNode = Node.newNode().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            property( "testPath", DateTime.now() ).
            build();

        final Property testProperty = myNode.property( "testPath" );

        assertNotNull( testProperty );
        assertTrue( testProperty.getValue() instanceof Value.DateTime );
    }

    @Test
    public void build_property_given_Value()
        throws Exception
    {

        final Node myNode = Node.newNode().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            property( "testPath", new Value.GeographicCoordinate( "79,80" ) ).
            build();

        final Property testProperty = myNode.property( "testPath" );

        assertNotNull( testProperty );
        assertTrue( testProperty.getValue() instanceof Value.GeographicCoordinate );
    }

    @Test
    public void build_given_path()
    {
        final Node myNode = Node.newNode().
            name( "myName" ).
            parent( NodePath.ROOT ).
            path( "test" ).
            build();

        final NodePath path = myNode.path();

        assertEquals( "test/myName", myNode.path().toString() );
    }

    @Test
    public void build_given_all_builder_properties()
        throws Exception
    {

        DateTime dateTime = new DateTime( 2013, 10, 25, 10, 43 );

        final Node myNode = Node.newNode().
            name( "myName" ).
            parent( NodePath.ROOT ).
            modifiedTime( dateTime ).
            createdTime( dateTime ).
            creator( UserKey.from( "test:creator" ) ).
            modifier( UserKey.from( "test:modifier" ) ).
            modifiedTime( dateTime ).
            path( "test" ).
            build();

        assertNotNull( myNode.name() );
        assertNotNull( myNode.parent() );
        assertNotNull( myNode.getModifiedTime() );
        assertNotNull( myNode.modifier() );
        assertNotNull( myNode.getCreatedTime() );
        assertNotNull( myNode.creator() );
        assertNotNull( myNode.path() );
    }

    @Test(expected = IllegalEditException.class)
    public void checkIllegalEdit_given_changed_parent_then_illegal()
        throws Exception
    {

        final Node myNode = Node.newNode( new EntityId( "myid" ) ).
            name( "myName" ).
            parent( NodePath.ROOT ).
            build();

        final Node myEditedNode = Node.newNode( new EntityId( "myid" ) ).
            name( "myName" ).
            parent( NodePath.newPath( "test" ).build() ).
            build();

        myNode.checkIllegalEdit( myEditedNode );
    }

    @Test(expected = IllegalEditException.class)
    public void checkIllegalEdit_given_changed_name_then_illegal()
        throws Exception
    {

        final Node myNode = Node.newNode( new EntityId( "myid" ) ).
            name( "myName" ).
            parent( NodePath.ROOT ).
            build();

        final Node myEditedNode = Node.newNode( new EntityId( "myid" ) ).
            name( "myChangedeName" ).
            parent( NodePath.ROOT ).
            build();

        myNode.checkIllegalEdit( myEditedNode );
    }

    @Test(expected = IllegalEditException.class)
    public void checkIllegalEdit_given_changed_modifier_then_illegal()
        throws Exception
    {

        final Node myNode = Node.newNode( new EntityId( "myid" ) ).
            name( "myName" ).
            parent( NodePath.ROOT ).
            build();

        final Node myEditedNode = Node.newNode( new EntityId( "myid" ) ).
            name( "myName" ).
            parent( NodePath.ROOT ).
            modifier( UserKey.from( "test:modifier" ) ).
            build();

        myNode.checkIllegalEdit( myEditedNode );
    }

    @Test
    public void checkIllegalEdit_given_changed_properties_allowed()
        throws Exception
    {

        final Node myNode = Node.newNode( new EntityId( "myid" ) ).
            name( "myName" ).
            parent( NodePath.ROOT ).
            build();

        final Node myEditedNode = Node.newNode( new EntityId( "myid" ) ).
            name( "myName" ).
            parent( NodePath.ROOT ).
            property( "data", "myData" ).
            build();

        myNode.checkIllegalEdit( myEditedNode );
    }

}
