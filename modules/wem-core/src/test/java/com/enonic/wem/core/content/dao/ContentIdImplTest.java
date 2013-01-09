package com.enonic.wem.core.content.dao;


import javax.jcr.Node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.ContentId;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ContentIdImplTest
{

    @Test
    public void contentId_create_instance()
        throws Exception
    {
        final Node node = Mockito.mock( Node.class );
        when( node.getIdentifier() ).thenReturn( "jcr-node-id" );
        final ContentId contentId = ContentIdFactory.from( node );

        assertEquals( "jcr-node-id", contentId.id() );
        assertEquals( contentId, contentId );
    }

    @Test
    public void contentId_equals()
        throws Exception
    {
        final Node node = Mockito.mock( Node.class );
        when( node.getIdentifier() ).thenReturn( "jcr-node-id" );
        final ContentId contentId = ContentIdFactory.from( node );

        final Node node2 = Mockito.mock( Node.class );
        when( node2.getIdentifier() ).thenReturn( "jcr-node-id" );
        final ContentId contentId2 = ContentIdFactory.from( node2 );

        final Node node3 = Mockito.mock( Node.class );
        when( node3.getIdentifier() ).thenReturn( "node-id-jcr" );
        final ContentId contentId3 = ContentIdFactory.from( node3 );

        assertEquals( contentId, contentId2 );
        assertEquals( contentId.hashCode(), contentId2.hashCode() );

        assertFalse( contentId.equals( contentId3 ) );
        assertFalse( contentId.hashCode() == contentId3.hashCode() );
    }

    @Test
    public void contentId_toString()
        throws Exception
    {
        final Node node = Mockito.mock( Node.class );
        when( node.getIdentifier() ).thenReturn( "jcr-node-id" );
        final ContentId contentId = ContentIdFactory.from( node );

        final Node node2 = Mockito.mock( Node.class );
        when( node2.getIdentifier() ).thenReturn( "jcr-node-id" );
        final ContentId contentId2 = ContentIdFactory.from( node2 );

        final Node node3 = Mockito.mock( Node.class );
        when( node3.getIdentifier() ).thenReturn( "node-id-jcr" );
        final ContentId contentId3 = ContentIdFactory.from( node3 );

        assertEquals( "jcr-node-id", contentId.toString() );
        assertEquals( "jcr-node-id", contentId.id() );
        assertEquals( contentId.toString(), contentId2.toString() );
        assertEquals( contentId.id(), contentId2.id() );

        assertFalse( contentId.toString().equals( contentId3.toString() ) );
        assertFalse( contentId.id().equals( contentId3.id() ) );
    }

    @Test(expected = NullPointerException.class)
    public void contentId_create_instance_with_null_throws_exception()
        throws Exception
    {
        final Node node = Mockito.mock( Node.class );
        when( node.getIdentifier() ).thenReturn( null );
        final ContentId contentId = ContentIdFactory.from( node );
    }
}
