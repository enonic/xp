package com.enonic.xp.admin.impl.rest.resource.schema.relationship;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.Instant;

import static org.junit.Assert.assertNotNull;

public class RelationshipTypeResourceTest
    extends AdminResourceTestSupport
{
    private RelationshipTypeService relationshipTypeService;

    private RelationshipTypeResource resource;

    @Override
    protected Object getResourceInstance()
    {
        resource = new RelationshipTypeResource();

        relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        resource.setRelationshipTypeService( relationshipTypeService );

        return resource;
    }

    @Test
    public void testRequestGetRelationshipTypeJson_existing()
        throws Exception
    {
        final RelationshipType relationshipType = RelationshipType.create().
            name( "myapplication:the_relationship_type" ).
            description( "RT description" ).
            build();

        final RelationshipTypeName name = RelationshipTypeName.from( "myapplication:the_relationship_type" );
        Mockito.when( relationshipTypeService.getByName( name ) ).thenReturn( relationshipType );

        String response =
            request().path( "schema/relationship" ).queryParam( "name", "myapplication:the_relationship_type" ).get().getAsString();

        assertJson( "get_relationship_type.json", response );

    }

    @Test
    public void testRequestGetRelationshipTypeJson_not_found()
        throws Exception
    {
        Mockito.when( relationshipTypeService.getByName( Mockito.any( RelationshipTypeName.class ) ) ).thenReturn( null );

        final MockRestResponse response =
            request().path( "schema/relationship" ).queryParam( "name", "myapplication:relationship_type" ).get();
        Assert.assertEquals( 404, response.getStatus() );
    }

    @Test
    public void testList()
        throws Exception
    {
        final RelationshipType relationshipType1 = RelationshipType.create().
            name( "myapplication:the_relationship_type_1" ).
            build();

        final RelationshipType relationshipType2 = RelationshipType.create().
            name( "myapplication:the_relationship_type_2" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType1, relationshipType2 );
        Mockito.when( relationshipTypeService.getAll() ).thenReturn( relationshipTypes );

        String response = request().path( "schema/relationship/list" ).get().getAsString();

        assertJson( "get_relationship_type_list.json", response );
    }


    @Test
    public void testRelationshipTypeIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "relationshipicon.png" ) );
        final Icon icon = Icon.from( data, "image/png", Instant.now() );

        RelationshipType relationshipType = RelationshipType.create().
            name( "myapplication:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "myapplication:person" ) ).
            addAllowedToType( ContentTypeName.from( "myapplication:person" ) ).
            icon( icon ).
            build();
        setupRelationshipType( relationshipType );

        // exercise
        final Response response = this.resource.getIcon( "myapplication:like", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testRelationshipTypeIconSvg()
            throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "relationshiptype.svg" ) );
        final Icon icon = Icon.from( data, "image/svg+xml", Instant.now() );

        RelationshipType relationshipType = RelationshipType.create().
                name( "myapplication:like" ).
                fromSemantic( "likes" ).
                toSemantic( "liked by" ).
                addAllowedFromType( ContentTypeName.from( "myapplication:person" ) ).
                addAllowedToType( ContentTypeName.from( "myapplication:person" ) ).
                icon( icon ).
                build();
        setupRelationshipType( relationshipType );

        final Response response = this.resource.getIcon( "myapplication:like", 20, null );

        assertNotNull( response.getEntity() );
        assertEquals( icon.getMimeType(), response.getMediaType().toString() );
        org.junit.Assert.assertArrayEquals( data, ( byte[] )response.getEntity() );
    }

    @Test
    public void testRelationshipTypeIcon_default_image()
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( "relationshiptype.svg" );
        final Response response = this.resource.getIcon( "myapplication:icon_svg_test", 20, null );

        assertNotNull( response.getEntity() );
        org.junit.Assert.assertArrayEquals( ByteStreams.toByteArray( in ), ( byte[] )response.getEntity() );
    }


    private void setupRelationshipType( final RelationshipType relationshipType )
    {
        Mockito.when( relationshipTypeService.getByName( relationshipType.getName() ) ).thenReturn( relationshipType );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }

}
