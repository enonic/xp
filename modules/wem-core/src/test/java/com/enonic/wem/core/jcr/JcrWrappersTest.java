package com.enonic.wem.core.jcr;

import java.io.IOException;
import java.util.Date;

import javax.jcr.RepositoryException;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.itest.AbstractSpringTest;

import static org.junit.Assert.*;

public class JcrWrappersTest
    extends AbstractSpringTest
{
    private static final double DELTA = 1e-15;

    @Autowired
    private JcrTemplate jcrTemplate;

    @Test
    public void jcrSessionTest()
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                testRootNode( session );
                testCreateRemove( session );
                testNodeSetGetProperties( session );
                testPropertyGetSet( session );

                return null;
            }
        } );
    }

    private void testRootNode( final JcrSession session )
    {
        final JcrNode root = session.getRootNode();
        assertNotNull( root );
        assertEquals( "/", root.getPath() );
        assertEquals( "", root.getName() );
    }

    private void testCreateRemove( final JcrSession session )
    {
        final JcrNode node1 = session.getOrCreateNode( "/node1" );
        assertNotNull( node1 );
        assertEquals( "/node1", node1.getPath() );
        assertEquals( "node1", node1.getName() );
        assertTrue( session.nodeExists( "/node1" ) );

        session.removeItem( "/node1" );
        assertFalse( session.nodeExists( "/node1" ) );
    }

    private void testNodeSetGetProperties( final JcrSession session )
    {
        final JcrNode node1 = session.getOrCreateNode( "/node1" );
        final JcrNode node2 = session.getOrCreateNode( "/node2" );
        node2.addMixin( "mix:referenceable" );

        final Date time = new Date();
        final DateTime dateTime = new DateTime( time );
        node1.setPropertyBoolean( "bool", true );
        node1.setPropertyBinary( "binary", "binarydata".getBytes() );
        node1.setPropertyDate( "date", time );
        node1.setPropertyDateTime( "datetime", dateTime );
        node1.setPropertyDouble( "double", 3.14 );
        node1.setPropertyLong( "long", 123 );
        node1.setPropertyReference( "ref", node2 );
        node1.setPropertyString( "string", "text" );

        assertEquals( true, node1.getPropertyBoolean( "bool" ) );
        assertEquals( false, node1.getPropertyBoolean( "bool2", false ) );
        assertArrayEquals( "binarydata".getBytes(), node1.getPropertyBinary( "binary" ) );
        assertEquals( time, node1.getPropertyDate( "date" ) );
        assertEquals( dateTime.getMillis(), node1.getPropertyDateTime( "datetime" ).getMillis() );
        assertEquals( 3.14, node1.getPropertyDouble( "double" ), DELTA );
        assertEquals( 123, node1.getPropertyLong( "long" ) );
        assertEquals( node2.getIdentifier(), node1.getPropertyReference( "ref" ).getIdentifier() );
        assertEquals( "text", node1.getPropertyString( "string" ) );
    }

    private void testPropertyGetSet( final JcrSession session )
    {
        final JcrNode node1 = session.getOrCreateNode( "/node1" );
        final JcrNode node2 = session.getOrCreateNode( "/node2" );
        final JcrNode node3 = session.getOrCreateNode( "/node3" );
        node2.addMixin( "mix:referenceable" );
        node3.addMixin( "mix:referenceable" );

        final Date time = new Date();
        final DateTime dateTime = new DateTime( time );
        node1.setPropertyBoolean( "bool", true );
        node1.setPropertyBinary( "binary", "binarydata".getBytes() );
        node1.setPropertyDate( "date", time );
        node1.setPropertyDateTime( "datetime", dateTime );
        node1.setPropertyDouble( "double", 3.14 );
        node1.setPropertyLong( "long", 123 );
        node1.setPropertyReference( "ref", node2 );
        node1.setPropertyString( "string", "text" );

        node1.getProperty( "bool" ).setValue( false );
        node1.getProperty( "binary" ).setValue( "binarydata".getBytes() );
        node1.getProperty( "date" ).setValue( time );
        node1.getProperty( "datetime" ).setValue( dateTime );
        node1.getProperty( "double" ).setValue( 2.71828 );
        node1.getProperty( "long" ).setValue( 456 );
        node1.getProperty( "ref" ).setValue( node3 );
        node1.getProperty( "string" ).setValue( "text" );

        assertEquals( false, node1.getProperty( "bool" ).getBoolean() );
        assertArrayEquals( "binarydata".getBytes(), node1.getProperty( "binary" ).getBinary() );
        assertEquals( time, node1.getProperty( "date" ).getDate() );
        assertEquals( dateTime.getMillis(), node1.getProperty( "datetime" ).getDateTime().getMillis() );
        assertEquals( 2.71828, node1.getProperty( "double" ).getDouble(), DELTA );
        assertEquals( 456, node1.getProperty( "long" ).getLong() );
        assertEquals( node3.getIdentifier(), node1.getProperty( "ref" ).getNode().getIdentifier() );
        assertEquals( "text", node1.getProperty( "string" ).getString() );
    }
}
