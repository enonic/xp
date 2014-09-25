package com.enonic.wem.admin.rest.resource.schema;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.mixin.GetMixinsParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;

public class SchemaIconResourceTest
{
    private SchemaIconResource resource;

    private MixinService mixinService;

    private ContentTypeService contentTypeService;

    private RelationshipTypeService relationshipTypeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.resource = new SchemaIconResource();

        mixinService = Mockito.mock( MixinService.class );
        this.resource.setMixinService( mixinService );

        contentTypeService = Mockito.mock( ContentTypeService.class );
        this.resource.setContentTypeService( contentTypeService );

        relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        this.resource.setRelationshipTypeService( relationshipTypeService );
    }

    @Test
    public void testContentTypeIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        Icon schemaIcon = Icon.from( data, "image/png", Instant.now() );

        final ContentType contentType = ContentType.newContentType().
            name( "mymodule:my_content_type" ).
            displayName( "My content type" ).
            superType( ContentTypeName.from( "mymodule:unstructured" ) ).
            icon( schemaIcon ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.resource.getSchemaIcon( "ContentType:mymodule:my_content_type", 20, null );
        final BufferedImage contentTypeIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( contentTypeIcon, 20 );
    }

    @Test
    public void testContentTypeIcon_fromSuperType()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        Icon schemaIcon = Icon.from( data, "image/png", Instant.now() );

        final ContentType systemContentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:unstructured" ).
            displayName( "Unstructured" ).
            icon( schemaIcon ).
            build();
        setupContentType( systemContentType );

        final ContentType contentType = ContentType.newContentType().
            name( "mymodule:my_content_type" ).
            displayName( "My content type" ).
            superType( systemContentType.getName() ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.resource.getSchemaIcon( "ContentType:mymodule:my_content_type", 20, null );
        final BufferedImage contentTypeIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( contentTypeIcon, 20 );
    }

    @Test(expected = javax.ws.rs.WebApplicationException.class)
    public void testContentTypeIcon_notFound()
        throws Exception
    {
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( null );

        try
        {
            // exercise
            this.resource.getSchemaIcon( "ContentType:mymodule:my_content_type", 10, null );
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
        byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        final Icon icon = Icon.from( data, "image/png", Instant.now() );

        Mixin mixin = newMixin().
            name( "mymodule:postal_code" ).
            displayName( "My content type" ).
            icon( icon ).
            addFormItem( newInput().name( "postal_code" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        // exercise
        final Response response = this.resource.getSchemaIcon( "Mixin:mymodule:postal_code", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testMixinIcon_default_image()
        throws Exception
    {
        Mixin mixin = newMixin().
            name( "mymodule:postal_code" ).
            displayName( "My content type" ).
            addFormItem( newInput().name( "postal_code" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        // exercise
        final Response response = this.resource.getSchemaIcon( "Mixin:mymodule:postal_code", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testRelationshipTypeIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        final Icon icon = Icon.from( data, "image/png", Instant.now() );

        RelationshipType relationshipType = newRelationshipType().
            name( "mymodule:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule:person" ) ).
            icon( icon ).
            build();
        setupRelationshipType( relationshipType );

        // exercise
        final Response response = this.resource.getSchemaIcon( "RelationshipType:mymodule:like", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testRelationshipTypeIcon_default_image()
        throws Exception
    {
        RelationshipType relationshipType = newRelationshipType().
            name( "mymodule:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule:person" ) ).
            build();
        setupRelationshipType( relationshipType );

        // exercise
        final Response response = this.resource.getSchemaIcon( "RelationshipType:mymodule:like", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    private void setupContentType( final ContentType contentType )
    {
        final List<ContentType> list = Lists.newArrayList();
        list.add( contentType );
        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentType.getName() );
        Mockito.when( contentTypeService.getByName( params ) ).thenReturn( contentType );
    }

    private void setupMixin( final Mixin mixin )
    {
        final List<Mixin> list = Lists.newArrayList();
        list.add( mixin );
        final Mixins result = Mixins.from( list );
        final GetMixinsParams params = new GetMixinsParams().names( MixinNames.from( mixin.getName() ) );
        Mockito.when( mixinService.getByNames( params ) ).thenReturn( result );
    }

    private void setupRelationshipType( final RelationshipType relationshipType )
    {
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( relationshipType.getName() );
        Mockito.when( relationshipTypeService.getByName( params ) ).thenReturn( relationshipType );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }
}
