package com.enonic.wem.admin.rest.resource.schema;

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
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;

public class SchemaImageResourceTest
{
    private SchemaImageResource controller;

    private Client client;

    @Before
    public void setUp()
        throws Exception
    {
        this.controller = new SchemaImageResource();
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
            name( "my_content_type" ).
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My content type" ).
            superType( new QualifiedContentTypeName( "system:unstructured" ) ).
            icon( icon ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.controller.getSchemaIcon( "ContentType:mymodule:my_content_type", 20 );
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
            module( ModuleName.SYSTEM ).
            displayName( "Unstructured" ).
            icon( icon ).
            build();
        setupContentType( systemContentType );

        final ContentType contentType = ContentType.newContentType().
            name( "my_content_type" ).
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My content type" ).
            superType( systemContentType.getQualifiedName() ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.controller.getSchemaIcon( "ContentType:mymodule:my_content_type", 20 );
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
            final Response response = this.controller.getSchemaIcon( "ContentType:mymodule:my_content_type", 10 );
            final BufferedImage contentTypeIcon = (BufferedImage) response.getEntity();
        }
        catch ( WebApplicationException e )
        {
            // verify
            assertEquals( 404, e.getResponse().getStatus() ); // HTTP Not Found
            throw e;
        }
    }

    @Test
    public void testMixinIcon()
        throws Exception
    {
        final byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        final Icon icon = Icon.from( data, "image/png" );

        Mixin mixin = newMixin().
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My content type" ).
            icon( icon ).
            formItem( newInput().name( "postal_code" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        // exercise
        final Response response = this.controller.getSchemaIcon( "Mixin:mymodule:postal_code", 20 );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testMixinIcon_default_image()
        throws Exception
    {
        Mixin mixin = newMixin().
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My content type" ).
            formItem( newInput().name( "postal_code" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        // exercise
        final Response response = this.controller.getSchemaIcon( "Mixin:mymodule:postal_code", 20 );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testRelationshipTypeIcon()
        throws Exception
    {
        final byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        final Icon icon = Icon.from( data, "image/png" );

        RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "mymodule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "mymodule:person" ) ).
            icon( icon ).
            build();
        setupRelationshipType( relationshipType );

        // exercise
        final Response response = this.controller.getSchemaIcon( "RelationshipType:mymodule:like", 20 );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testRelationshipTypeIcon_default_image()
        throws Exception
    {
        RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "mymodule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "mymodule:person" ) ).
            build();
        setupRelationshipType( relationshipType );

        // exercise
        final Response response = this.controller.getSchemaIcon( "RelationshipType:mymodule:like", 20 );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    private void setupContentType( final ContentType contentType )
    {
        final List<ContentType> list = Lists.newArrayList();
        list.add( contentType );
        final ContentTypes result = ContentTypes.from( list );
        final GetContentTypes command =
            new GetContentTypes().qualifiedNames( QualifiedContentTypeNames.from( contentType.getQualifiedName() ) );
        Mockito.when( client.execute( command ) ).thenReturn( result );
    }

    private void setupMixin( final Mixin mixin )
    {
        final List<Mixin> list = Lists.newArrayList();
        list.add( mixin );
        final Mixins result = Mixins.from( list );
        final GetMixins command = new GetMixins().names( QualifiedMixinNames.from( mixin.getQualifiedName() ) );
        Mockito.when( client.execute( command ) ).thenReturn( result );
    }

    private void setupRelationshipType( final RelationshipType relationshipType )
    {
        final List<RelationshipType> list = Lists.newArrayList();
        list.add( relationshipType );
        final RelationshipTypes result = RelationshipTypes.from( list );
        final GetRelationshipTypes command =
            new GetRelationshipTypes().qualifiedNames( QualifiedRelationshipTypeNames.from( relationshipType.getQualifiedName() ) );
        Mockito.when( client.execute( command ) ).thenReturn( result );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }
}
