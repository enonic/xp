package com.enonic.xp.admin.impl.rest.resource.relationship;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.admin.impl.rest.resource.MockRestResponse;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.relationship.CreateRelationshipParams;
import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.relationship.RelationshipId;
import com.enonic.xp.relationship.RelationshipKey;
import com.enonic.xp.relationship.RelationshipNotFoundException;
import com.enonic.xp.relationship.RelationshipService;
import com.enonic.xp.relationship.Relationships;
import com.enonic.xp.relationship.UpdateRelationshipFailureException;
import com.enonic.xp.relationship.UpdateRelationshipParams;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

import static org.mockito.Matchers.isA;

public class RelationshipResourceTest
    extends AbstractResourceTest
{
    private RelationshipService relationshipService;

    @Test
    public void get_from_content_with_one_relationship()
        throws Exception
    {
        Relationships relationships = Relationships.from( Relationship.create().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( RelationshipTypeName.REFERENCE ).
            build() );

        Mockito.when( this.relationshipService.getAll( isA( ContentId.class ) ) ).thenReturn( relationships );

        String result = request().path( "relationship" ).queryParam( "fromContent", "111" ).get().getAsString();

        assertJson( "get_relationship.json", result );
    }

    @Test
    public void get_from_content_with_two_relationships()
        throws Exception
    {
        Relationships relationships = Relationships.from( Relationship.create().fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( RelationshipTypeName.REFERENCE ).
            build(), Relationship.create().fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "333" ) ).
            type( RelationshipTypeName.REFERENCE ).
            build() );

        Mockito.when( this.relationshipService.getAll( isA( ContentId.class ) ) ).thenReturn( relationships );

        String result = request().path( "relationship" ).queryParam( "fromContent", "111" ).get().getAsString();

        assertJson( "get_relationships.json", result );
    }

    @Test
    public void create()
        throws Exception
    {
        Mockito.when( this.relationshipService.create( isA( CreateRelationshipParams.class ) ) ).thenReturn( new RelationshipId()
        {
            @Override
            public String toString()
            {
                return "111";
            }
        } );

        String result = request().path( "relationship/create" ).
            entity( readFromFile( "create_relationship_params.json" ), MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertJson( "create_relationship.json", result );
    }

    @Test
    public void update_success()
        throws Exception
    {
        String result = request().path( "relationship/update" ).entity( readFromFile( "update_relationship_params.json" ),
                                                                        MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertJson( "update_relationship.json", result );
    }

    @Test
    public void update_failure()
        throws Exception
    {
        RelationshipKey relationshipKey = RelationshipKey.create().
            type( RelationshipTypeName.REFERENCE ).
            fromContent( ContentId.from( "123" ) ).
            toContent( ContentId.from( "321" ) ).
            build();

        UpdateRelationshipFailureException exception =
            UpdateRelationshipFailureException.create().relationshipKey( relationshipKey ).failure(
            new RelationshipNotFoundException( relationshipKey ) ).build();

        Mockito.doThrow( exception ).when( this.relationshipService ).update( isA( UpdateRelationshipParams.class ) );

        final MockRestResponse response = request().path( "relationship/update" ).entity( readFromFile( "update_relationship_params.json" ),
                                                                                          MediaType.APPLICATION_JSON_TYPE ).post();

        Assert.assertEquals( 404, response.getStatus() );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.relationshipService = Mockito.mock( RelationshipService.class );

        final RelationshipResource resource = new RelationshipResource();
        resource.setRelationshipService( relationshipService );
        return resource;
    }
}
