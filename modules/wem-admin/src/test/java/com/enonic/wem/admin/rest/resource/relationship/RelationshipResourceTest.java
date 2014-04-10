package com.enonic.wem.admin.rest.resource.relationship;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.UniformInterfaceException;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.CreateRelationshipParams;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.RelationshipNotFoundException;
import com.enonic.wem.api.relationship.RelationshipService;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.api.relationship.UpdateRelationshipFailureException;
import com.enonic.wem.api.relationship.UpdateRelationshipParams;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

import static com.enonic.wem.api.relationship.Relationship.newRelationship;
import static com.enonic.wem.api.relationship.UpdateRelationshipFailureException.newUpdateRelationshipsResult;
import static org.mockito.Matchers.isA;

public class RelationshipResourceTest
    extends AbstractResourceTest
{
    private RelationshipService relationshipService;

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    public void get_from_content_with_one_relationship()
        throws Exception
    {
        Relationships relationships = Relationships.from( newRelationship().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( RelationshipTypeName.DEFAULT ).
            build() );

        Mockito.when( this.relationshipService.getAll( isA( ContentId.class ) ) ).thenReturn( relationships );

        String result = resource().path( "relationship" ).queryParam( "fromContent", "111" ).get( String.class );

        assertJson( "get_relationship.json", result );
    }

    @Test
    public void get_from_content_with_two_relationships()
        throws Exception
    {
        Relationships relationships = Relationships.from( newRelationship().fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( RelationshipTypeName.DEFAULT ).
            build(), newRelationship().fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "333" ) ).
            type( RelationshipTypeName.DEFAULT ).
            build() );

        Mockito.when( this.relationshipService.getAll( isA( ContentId.class ) ) ).thenReturn( relationships );

        String result = resource().path( "relationship" ).queryParam( "fromContent", "111" ).get( String.class );

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

        String result = resource().path( "relationship/create" ).
            entity( readFromFile( "create_relationship_params.json" ), MediaType.APPLICATION_JSON_TYPE ).post( String.class );

        assertJson( "create_relationship.json", result );
    }

    @Test
    public void update_success()
        throws Exception
    {
        String result = resource().path( "relationship/update" ).entity( readFromFile( "update_relationship_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );

        assertJson( "update_relationship.json", result );
    }

    @Test
    public void update_failure()
        throws Exception
    {
        RelationshipKey relationshipKey = RelationshipKey.newRelationshipKey().
            type( RelationshipTypeName.LIKE ).
            fromContent( ContentId.from( "123" ) ).
            toContent( ContentId.from( "321" ) ).
            build();

        UpdateRelationshipFailureException exception = newUpdateRelationshipsResult().relationshipKey( relationshipKey ).failure(
            new RelationshipNotFoundException( relationshipKey ) ).build();

        Mockito.doThrow( exception ).when( this.relationshipService ).update( isA( UpdateRelationshipParams.class ) );

        try
        {
            resource().path( "relationship/update" ).entity( readFromFile( "update_relationship_params.json" ),
                                                             MediaType.APPLICATION_JSON_TYPE ).post( String.class );
            Assert.assertFalse( "Exception should've been thrown by this time", true );
        }
        catch ( UniformInterfaceException e )
        {
            Assert.assertEquals( 404, e.getResponse().getStatus() );
            Assert.assertEquals(
                "Failed to update Relationship [RelationshipKey{fromContent=123, toContent=321, type=like, managingData=null}]:\n" +
                    "Failure #1: Relationship [RelationshipKey{fromContent=123, toContent=321, type=like, managingData=null}] was not found\n",
                e.getResponse().getEntity( String.class )
            );
        }
    }

    @Override
    protected Object getResourceInstance()
    {
        this.relationshipService = Mockito.mock( RelationshipService.class );

        final RelationshipResource resource = new RelationshipResource();
        resource.relationshipService = relationshipService;
        return resource;
    }
}
