package com.enonic.wem.admin.rest.resource.relationship;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.TestUtil;
import com.enonic.wem.admin.rest.resource.relationship.model.RelationshipJson;
import com.enonic.wem.admin.rest.resource.relationship.model.RelationshipListJson;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.relationship.GetRelationships;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.relationship.Relationship.newRelationship;
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
}
