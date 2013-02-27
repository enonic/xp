package com.enonic.wem.core.content.relationship.dao;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.Relationships;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentDaoImpl;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.relationship.RelationshipKey.newRelationshipKey;
import static com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName.LINK;
import static com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName.PARENT;
import static org.junit.Assert.*;

public class RelationshipDaoImplTest
    extends AbstractJcrTest
{
    private static final DateTime NOW = new DateTime( 2013, 1, 1, 12, 0, DateTimeZone.UTC );

    private ContentDao contentDao;

    private RelationshipDao relationshipDao;

    public void setupDao()
        throws Exception
    {
        session.getNode( "/wem/spaces" ).addNode( "myspace" );
        contentDao = new ContentDaoImpl();
        relationshipDao = new RelationshipDaoImpl();
    }

    @Test
    public void given_relationship_when_create_then_RelationshipId_is_returned()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        ContentId contentA = contentDao.create( createContent( "myspace:a" ), session );
        ContentId contentB = contentDao.create( createContent( "myspace:b" ), session );
        commit();

        // exercise
        RelationshipId relationshipId = relationshipDao.create( createRelationship( contentA, contentB, PARENT ), session );
        commit();

        // verify
        assertNotNull( relationshipId );
    }

    @Test
    public void given_one_persisted_relationship_when_select_by_matching_key_then_relationship_is_returned()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        ContentId contentA = contentDao.create( createContent( "myspace:a" ), session );
        ContentId contentB = contentDao.create( createContent( "myspace:b" ), session );
        commit();

        relationshipDao.create( createRelationship( contentA, contentB, PARENT ), session );
        commit();

        // exercise
        Relationship storedRelationship = relationshipDao.select( newRelationshipKey().
            type( PARENT ).
            fromContent( contentA ).
            toContent( contentB ).build(), session );

        // verify
        assertEquals( contentA, storedRelationship.getFromContent() );
        assertEquals( contentB, storedRelationship.getToContent() );
        assertEquals( PARENT, storedRelationship.getType() );
    }

    @Test
    public void given_two_persisted_relationships_differing_only_by_type_when_select_by_matching_key_then_a_relationship_is_returned()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        ContentId contentA = contentDao.create( createContent( "myspace:a" ), session );
        ContentId contentB = contentDao.create( createContent( "myspace:b" ), session );
        commit();

        relationshipDao.create( createRelationship( contentA, contentB, LINK ), session );
        relationshipDao.create( createRelationship( contentA, contentB, PARENT ), session );
        commit();

        // exercise
        Relationship storedRelationship = relationshipDao.select( newRelationshipKey().
            type( PARENT ).
            fromContent( contentA ).
            toContent( contentB ).build(), session );

        // verify
        assertEquals( contentA, storedRelationship.getFromContent() );
        assertEquals( contentB, storedRelationship.getToContent() );
        assertEquals( PARENT, storedRelationship.getType() );
    }

    @Test
    public void given_two_persisted_relationships_differing_only_by_managingData_when_select_by_matching_key_then_relationship_is_returned()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        ContentId contentA = contentDao.create( createContent( "myspace:a" ), session );
        ContentId contentB = contentDao.create( createContent( "myspace:b" ), session );
        commit();

        relationshipDao.create( createRelationship( contentA, contentB, PARENT ), session );
        relationshipDao.create( createRelationship( contentA, contentB, PARENT, EntryPath.from( "myData" ) ), session );
        commit();

        // exercise
        Relationship storedRelationship = relationshipDao.select( newRelationshipKey().
            type( PARENT ).
            fromContent( contentA ).
            toContent( contentB ).
            managingData( EntryPath.from( "myData" ) ).build(), session );

        // verify
        assertEquals( contentA, storedRelationship.getFromContent() );
        assertEquals( contentB, storedRelationship.getToContent() );
        assertEquals( PARENT, storedRelationship.getType() );
        assertEquals( EntryPath.from( "myData" ), storedRelationship.getManagingData() );
    }

    @Test
    public void selectByIds()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        ContentId contentA = contentDao.create( createContent( "myspace:a" ), session );
        ContentId contentB = contentDao.create( createContent( "myspace:b" ), session );
        commit();

        RelationshipId id1 = relationshipDao.create( createRelationship( contentA, contentB, PARENT ), session );
        RelationshipId id2 = relationshipDao.create( createRelationship( contentA, contentB, LINK ), session );
        commit();

        // exercise
        Relationships storedRelationships = relationshipDao.select( RelationshipIds.from( id1, id2 ), session );

        // verify
        Relationship storedRelationship1 = storedRelationships.get( 0 );
        assertEquals( contentA, storedRelationship1.getFromContent() );
        assertEquals( contentB, storedRelationship1.getToContent() );
        assertEquals( PARENT, storedRelationship1.getType() );

        Relationship storedRelationship2 = storedRelationships.get( 1 );
        assertEquals( contentA, storedRelationship2.getFromContent() );
        assertEquals( contentB, storedRelationship2.getToContent() );
        assertEquals( LINK, storedRelationship2.getType() );
    }

    private Relationship createRelationship( final ContentId contentA, final ContentId contentB, final QualifiedRelationshipTypeName type )
    {
        return createRelationship( contentA, contentB, type, null );
    }

    private Relationship createRelationship( final ContentId contentA, final ContentId contentB, final QualifiedRelationshipTypeName type,
                                             final EntryPath managingData )
    {
        return Relationship.newRelationship().
            type( type ).
            fromContent( contentA ).
            toContent( contentB ).
            managed( managingData ).
            creator( AccountKey.superUser() ).
            createdTime( NOW ).
            build();
    }

    private Content createContent( String path )
    {
        return newContent().path( ContentPath.from( path ) ).build();
    }
}
