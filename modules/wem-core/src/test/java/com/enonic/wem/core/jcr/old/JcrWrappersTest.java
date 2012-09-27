package com.enonic.wem.core.jcr.old;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    public void jcrRootNodeTest()
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                testRootNode( session );
                return null;
            }
        } );
    }

    @Test
    public void jcrCreateRemoveTest()
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                testCreateRemove( session );
                return null;
            }
        } );
    }

    @Test
    public void jcrNodeSetGetPropertiesTest()
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                testNodeSetGetProperties( session );
                return null;
            }
        } );
    }

    @Test
    public void jcrPropertyGetSetTest()
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                testPropertyGetSet( session );
                return null;
            }
        } );
    }

    @Test
    public void jcrQueryTest()
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                testQuery( session );
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
        // assertEquals( node2.getIdentifier(), node1.getPropertyReference( "ref" ).getIdentifier() );
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
        // assertEquals( node3.getIdentifier(), node1.getProperty( "ref" ).getNode().getIdentifier() );
        assertEquals( "text", node1.getProperty( "string" ).getString() );
    }

    private void testQuery( final JcrSession session )
    {
        final JcrNode root = session.getRootNode();
        final JcrNode node1 = root.addNode( "node1", "nt:resource" );
        node1.setPropertyBinary( "jcr:data", "data".getBytes() );
        node1.setPropertyString( "jcr:mimeType", "text/plain" );
        node1.setPropertyDate( "jcr:lastModified", new Date() );

        final JcrNode node2 = root.addNode( "node2" );
        final JcrNode node2A = node2.addNode( "node2A" );
        final JcrNode node2B = node2.addNode( "node2B" );
        node2A.setPropertyLong( "size", 13 );
        node2A.setPropertyBoolean( "enabled", true );
        node2A.setPropertyDouble( "val", 1.2 );
        node2A.setPropertyString( "descr", "Test 1" );
        node2B.setPropertyLong( "size", 45 );
        node2B.setPropertyBoolean( "enabled", false );
        node2B.setPropertyDouble( "val", 3.4 );
        node2B.setPropertyString( "descr", "Test 2" );

        final JcrNode node3 = root.addNode( "node3" );
        final JcrNode node3A = node3.addNode( "node3A" );
        final JcrNode node3B = node3.addNode( "node3B" );
        node3A.setPropertyLong( "size", 67 );
        node3B.setPropertyLong( "size", 45 );
        session.save();

        // by node type
        final JcrQuery query = session.createQuery().selectNodeType( "nt:resource" );
        final JcrNodeIterator results = query.execute();

        assertTrue( results.hasNext() );
        final JcrNode result = results.nextNode();
        assertEquals( "/node1", result.getPath() );

        // by node name
        final JcrQuery queryName = session.createQuery().withName( "node2" );
        final JcrNodeIterator results2 = queryName.execute();

        assertTrue( results2.hasNext() );
        final JcrNode result2 = results2.nextNode();
        assertEquals( "/node2", result2.getPath() );

        // from path
        final JcrQuery queryPath = session.createQuery().from( "/node2" );
        final JcrNodeIterator resultsQueryPath = queryPath.execute();

        assertTrue( resultsQueryPath.hasNext() );
        assertEquals( 2, resultsQueryPath.getSize() );
        final Set<String> results3NodePaths = new HashSet<String>();
        while ( resultsQueryPath.hasNext() )
        {
            results3NodePaths.add( resultsQueryPath.next().getPath() );
        }
        assertTrue( results3NodePaths.contains( "/node2/node2A" ) );
        assertTrue( results3NodePaths.contains( "/node2/node2B" ) );

        // by property value (long)
        final JcrQuery queryPropertyLong = session.createQuery().withPropertyEqualsTo( "size", 45 );
        final JcrNodeIterator resultsQueryPropertyLong = queryPropertyLong.execute();

        assertTrue( resultsQueryPropertyLong.hasNext() );
        assertEquals( 2, resultsQueryPropertyLong.getSize() );
        final Set<String> nodePathsLong = new HashSet<String>();
        while ( resultsQueryPropertyLong.hasNext() )
        {
            nodePathsLong.add( resultsQueryPropertyLong.next().getPath() );
        }
        assertTrue( nodePathsLong.contains( "/node2/node2B" ) );
        assertTrue( nodePathsLong.contains( "/node3/node3B" ) );

        // by property value (double)
        final JcrQuery queryPropertyDouble = session.createQuery().withPropertyEqualsTo( "val", 1.2 );
        final JcrNodeIterator resultsQueryPropertyDouble = queryPropertyDouble.execute();

        assertTrue( resultsQueryPropertyDouble.hasNext() );
        assertEquals( 1, resultsQueryPropertyDouble.getSize() );
        final Set<String> nodePathsDouble = new HashSet<String>();
        while ( resultsQueryPropertyDouble.hasNext() )
        {
            nodePathsDouble.add( resultsQueryPropertyDouble.next().getPath() );
        }
        assertTrue( nodePathsDouble.contains( "/node2/node2A" ) );

        // by property value (string)
        final JcrQuery queryPropertyString = session.createQuery().withPropertyEqualsTo( "descr", "Test 2" );
        final JcrNodeIterator resultsQueryPropertyString = queryPropertyString.execute();

        assertTrue( resultsQueryPropertyString.hasNext() );
        assertEquals( 1, resultsQueryPropertyString.getSize() );
        final Set<String> nodePathsString = new HashSet<String>();
        while ( resultsQueryPropertyString.hasNext() )
        {
            nodePathsString.add( resultsQueryPropertyString.next().getPath() );
        }
        assertTrue( nodePathsString.contains( "/node2/node2B" ) );

        // by property value (boolean)
        final JcrQuery queryPropertyBool = session.createQuery().withPropertyEqualsTo( "enabled", true );
        final JcrNodeIterator resultsQueryPropertyBool = queryPropertyBool.execute();

        assertTrue( resultsQueryPropertyBool.hasNext() );
        assertEquals( 1, resultsQueryPropertyBool.getSize() );
        final Set<String> nodePathsBool = new HashSet<String>();
        while ( resultsQueryPropertyBool.hasNext() )
        {
            nodePathsBool.add( resultsQueryPropertyBool.next().getPath() );
        }
        assertTrue( nodePathsBool.contains( "/node2/node2A" ) );

    }
}
