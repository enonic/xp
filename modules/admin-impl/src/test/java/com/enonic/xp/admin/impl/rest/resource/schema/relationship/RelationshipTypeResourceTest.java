package com.enonic.xp.admin.impl.rest.resource.schema.relationship;

import java.awt.image.BufferedImage;
import java.time.Instant;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Resources;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.admin.impl.rest.resource.MockRestResponse;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;

import static com.enonic.xp.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;

public class RelationshipTypeResourceTest
    extends AbstractResourceTest
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
        final RelationshipType relationshipType = newRelationshipType().
            name( "mymodule:the_relationship_type" ).
            description( "RT description" ).
            build();

        final RelationshipTypeName name = RelationshipTypeName.from( "mymodule:the_relationship_type" );
        Mockito.when( relationshipTypeService.getByName( name ) ).thenReturn( relationshipType );

        String response =
            request().path( "schema/relationship" ).queryParam( "name", "mymodule:the_relationship_type" ).get().getAsString();

        assertJson( "get_relationship_type.json", response );

    }

    @Test
    public void testRequestGetRelationshipTypeJson_not_found()
        throws Exception
    {
        Mockito.when( relationshipTypeService.getByName( Mockito.any( RelationshipTypeName.class ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "schema/relationship" ).queryParam( "name", "mymodule:relationship_type" ).get();
        Assert.assertEquals( 404, response.getStatus() );
    }

    @Test
    public void testList()
        throws Exception
    {
        final RelationshipType relationshipType1 = newRelationshipType().
            name( "mymodule:the_relationship_type_1" ).
            build();

        final RelationshipType relationshipType2 = newRelationshipType().
            name( "mymodule:the_relationship_type_2" ).
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
        final Response response = this.resource.getIcon( "mymodule:like", 20, null );
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
        final Response response = this.resource.getIcon( "mymodule:like", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
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
