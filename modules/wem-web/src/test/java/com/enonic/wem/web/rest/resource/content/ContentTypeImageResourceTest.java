package com.enonic.wem.web.rest.resource.content;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;

import static org.junit.Assert.*;

public class ContentTypeImageResourceTest
{
    private ContentTypeImageResource controller;

    private Client client;

    @Before
    public void setUp()
        throws Exception
    {
        this.controller = new ContentTypeImageResource();
        client = Mockito.mock( Client.class );
        this.controller.setClient( client );
    }

    @Test
    public void testContentTypeIcon()
        throws Exception
    {
        final byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        final Icon icon = Icon.from( data, "image/png" );

        final ContentType contentType = ContentType.newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My content type" ).
            superType( new QualifiedContentTypeName( "System:unstructured" ) ).
            icon( icon ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.controller.getContentTypeIcon( "myModule:myContentType", 20 );
        final BufferedImage contentTypeIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( contentTypeIcon, 20 );
    }

    @Test
    public void testContentTypeIcon_fromSuperType()
        throws Exception
    {
        final byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        final Icon icon = Icon.from( data, "image/png" );

        final ContentType systemContentType = ContentType.newContentType().
            name( "unstructured" ).
            module( ModuleName.from( "System" ) ).
            displayName( "Unstructured" ).
            icon( icon ).
            build();
        setupContentType( systemContentType );

        final ContentType contentType = ContentType.newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My content type" ).
            superType( systemContentType.getQualifiedName() ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.controller.getContentTypeIcon( "myModule:myContentType", 20 );
        final BufferedImage contentTypeIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( contentTypeIcon, 20 );
    }

    @Test(expected = javax.ws.rs.WebApplicationException.class)
    public void testContentTypeIcon_notFound()
        throws Exception
    {
        final ContentTypes emptyContentTypes = ContentTypes.empty();
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn( emptyContentTypes );

        try
        {
            // exercise
            final Response response = this.controller.getContentTypeIcon( "myModule:myContentType", 10 );
            final BufferedImage contentTypeIcon = (BufferedImage) response.getEntity();
        }
        catch ( WebApplicationException e )
        {
            // verify
            assertEquals( 404, e.getResponse().getStatus() ); // HTTP Not Found
            throw e;
        }
    }

    private void setupContentType( final ContentType contentType )
    {
        final List<ContentType> list = Lists.newArrayList();
        list.add( contentType );
        final ContentTypes result = ContentTypes.from( list );
        final GetContentTypes command = new GetContentTypes().names( QualifiedContentTypeNames.from( contentType.getQualifiedName() ) );
        Mockito.when( client.execute( command ) ).thenReturn( result );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }
}
