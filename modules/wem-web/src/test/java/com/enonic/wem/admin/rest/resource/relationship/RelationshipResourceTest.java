package com.enonic.wem.admin.rest.resource.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.TestUtil;
import com.enonic.wem.admin.rest.resource.relationship.model.CreateRelationshipJson;
import com.enonic.wem.admin.rest.resource.relationship.model.RelationshipJson;
import com.enonic.wem.admin.rest.resource.relationship.model.RelationshipListJson;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.relationship.CreateRelationship;
import com.enonic.wem.api.command.relationship.GetRelationships;
import com.enonic.wem.api.command.relationship.UpdateRelationship;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.RelationshipNotFoundException;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.api.relationship.UpdateRelationshipFailureException;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.relationship.dao.RelationshipIdFactory;

import static com.enonic.wem.api.relationship.Relationship.newRelationship;
import static com.enonic.wem.api.relationship.UpdateRelationshipFailureException.newUpdateRelationshipsResult;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;

public class RelationshipResourceTest
{
    private Client client;

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );
    }

    @Test
    public void get_from_content_with_one_relationship()
        throws Exception
    {
        Relationships relationships = Relationships.from( newRelationship().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            build() );
        Mockito.when( client.execute( isA( GetRelationships.class ) ) ).thenReturn( relationships );

        final RelationshipResource resource = new RelationshipResource();
        resource.setClient( client );

        RelationshipListJson result = resource.get( "111" );
        assertEquals( 1, result.getTotal() );

        List<String> names = new ArrayList<>( 1 );
        for ( final RelationshipJson model : result.getRelationships() )
        {
            names.add( model.getToContent() );
        }

        TestUtil.assertUnorderedArraysEquals( new String[]{"222"}, names.toArray() );
    }

    @Test
    public void get_from_content_with_two_relationships()
        throws Exception
    {
        Relationships relationships = Relationships.from( newRelationship().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            build(), newRelationship().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "333" ) ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            build() );
        Mockito.when( client.execute( isA( GetRelationships.class ) ) ).thenReturn( relationships );

        final RelationshipResource resource = new RelationshipResource();
        resource.setClient( client );

        RelationshipListJson result = resource.get( "111" );
        assertEquals( 2, result.getTotal() );

        List<String> names = new ArrayList<>( 2 );
        for ( final RelationshipJson model : result.getRelationships() )
        {
            names.add( model.getToContent() );
        }

        TestUtil.assertUnorderedArraysEquals( new String[]{"222", "333"}, names.toArray() );
    }

    @Test
    public void testCreate()
        throws Exception
    {
        RelationshipId relationshipId = RelationshipIdFactory.from( "123321" );
        Mockito.when( client.execute( isA( CreateRelationship.class ) ) ).thenReturn( relationshipId );

        final RelationshipResource resource = new RelationshipResource();
        resource.setClient( client );

        final Map<String, String> properties = new HashMap<>();
        properties.put( "a", "1" );
        properties.put( "b", "2" );
        properties.put( "c", "3" );

        CreateRelationshipJson result = resource.create( QualifiedRelationshipTypeName.LIKE.toString(), "123", "321", properties );
        assertNotNull( result );
        assertEquals( "123", result.getFromContent() );
        assertEquals( "321", result.getToContent() );
        assertEquals( "system:like", result.getType() );
    }

    @Test
    public void update()
        throws Exception
    {
        RelationshipKey relationshipKey = RelationshipKey.newRelationshipKey().
            type( QualifiedRelationshipTypeName.LIKE ).
            fromContent( ContentId.from( "123" ) ).
            toContent( ContentId.from( "321" ) ).
            build();
        UpdateRelationshipFailureException.Builder result = newUpdateRelationshipsResult();
        result.relationshipKey( relationshipKey );
        Mockito.when( client.execute( isA( UpdateRelationship.class ) ) ).thenReturn( result.build() );

        final RelationshipResource resource = new RelationshipResource();
        resource.setClient( client );

        final Map<String, String> propertiesToAdd = new HashMap<>();
        propertiesToAdd.put( "addA", "1" );
        propertiesToAdd.put( "addB", "2" );

        final String[] remove = new String[] {"remove1", "remove2"};

        resource.update( relationshipKey, propertiesToAdd, remove );
    }

    @Test(expected = WebApplicationException.class)
    public void update_with_failure()
        throws Exception
    {
        RelationshipKey relationshipKey = RelationshipKey.newRelationshipKey().
            type( QualifiedRelationshipTypeName.LIKE ).
            fromContent( ContentId.from( "123" ) ).
            toContent( ContentId.from( "321" ) ).
            build();
        UpdateRelationshipFailureException.Builder result = newUpdateRelationshipsResult().
            relationshipKey( relationshipKey ).
            failure( new RelationshipNotFoundException( relationshipKey ) );
        Mockito.when( client.execute( isA( UpdateRelationship.class ) ) ).thenThrow( result.build() );

        final RelationshipResource resource = new RelationshipResource();
        resource.setClient( client );

        resource.update( relationshipKey, null, null );
    }
}
