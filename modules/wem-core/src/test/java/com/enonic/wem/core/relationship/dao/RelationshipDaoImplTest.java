package com.enonic.wem.core.relationship.dao;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.data.data.DataPath;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentDaoImpl;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.relationship.RelationshipKey.newRelationshipKey;
import static com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName.LIKE;
import static com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName.LINK;
import static com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName.PARENT;
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
        relationshipDao.create( createRelationship( contentA, contentB, PARENT, DataPath.from( "myData" ) ), session );
        commit();

        // exercise
        Relationship storedRelationship = relationshipDao.select( newRelationshipKey().
            type( PARENT ).
            fromContent( contentA ).
            toContent( contentB ).
            managingData( DataPath.from( "myData" ) ).build(), session );

        // verify
        assertEquals( contentA, storedRelationship.getFromContent() );
        assertEquals( contentB, storedRelationship.getToContent() );
        assertEquals( PARENT, storedRelationship.getType() );
        assertEquals( DataPath.from( "myData" ), storedRelationship.getManagingData() );
    }

    @Test
    public void given_persisted_relationship_with_managingData_with_more_than_one_path_element_when_select_by_matching_key_then_relationship_is_returned()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        ContentId contentA = contentDao.create( createContent( "myspace:a" ), session );
        ContentId contentB = contentDao.create( createContent( "myspace:b" ), session );
        commit();

        relationshipDao.create( createRelationship( contentA, contentB, PARENT, DataPath.from( "myParent[3].myData[1]" ) ), session );
        commit();

        // exercise
        Relationship storedRelationship = relationshipDao.select( newRelationshipKey().
            type( PARENT ).
            fromContent( contentA ).
            toContent( contentB ).
            managingData( DataPath.from( "myParent[3].myData[1]" ) ).build(), session );

        // verify
        assertEquals( contentA, storedRelationship.getFromContent() );
        assertEquals( contentB, storedRelationship.getToContent() );
        assertEquals( PARENT, storedRelationship.getType() );
        assertEquals( DataPath.from( "myParent[3].myData[1]" ), storedRelationship.getManagingData() );
    }

    @Test
    public void given_two_persisted_relationships_from_same_content_when_selectFromContent_then_two_relationships_are_returned()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        ContentId contentIdA = contentDao.create( createContent( "myspace:a" ), session );
        ContentId contentIdB = contentDao.create( createContent( "myspace:b" ), session );
        ContentId contentIdC = contentDao.create( createContent( "myspace:c" ), session );
        ContentId contentIdD = contentDao.create( createContent( "myspace:d" ), session );
        commit();

        relationshipDao.create(
            createRelationship( contentIdA, contentIdB, PARENT, DataPath.from( "myParent[3].myData[1]" ), NOW.plusSeconds( 1 ) ), session );
        relationshipDao.create( createRelationship( contentIdA, contentIdC, PARENT, NOW.plusSeconds( 2 ) ), session );
        relationshipDao.create( createRelationship( contentIdA, contentIdC, LIKE, NOW.plusSeconds( 3 ) ), session );
        relationshipDao.create( createRelationship( contentIdD, contentIdB, PARENT, NOW.plusSeconds( 4 ) ), session );
        commit();

        // exercise
        Relationships storedRelationships = sortOnCreatedTime( relationshipDao.selectFromContent( contentIdA, session ) );

        // verify
        assertEquals( 3, storedRelationships.getSize() );

        Relationship parentRelationshipToB = storedRelationships.get( 0 );
        assertEquals( contentIdB, parentRelationshipToB.getToContent() );
        assertEquals( DataPath.from( "myParent[3].myData[1]" ), parentRelationshipToB.getManagingData() );
        assertEquals( PARENT, parentRelationshipToB.getType() );

        Relationship parentRelationshipToC = storedRelationships.get( 1 );
        assertEquals( contentIdC, parentRelationshipToC.getToContent() );
        assertEquals( null, parentRelationshipToC.getManagingData() );
        assertEquals( PARENT, parentRelationshipToC.getType() );

        Relationship likeRelationshipToC = storedRelationships.get( 2 );
        assertEquals( contentIdC, likeRelationshipToC.getToContent() );
        assertEquals( null, likeRelationshipToC.getManagingData() );
        assertEquals( LIKE, likeRelationshipToC.getType() );
    }

    private Relationship createRelationship( final ContentId contentA, final ContentId contentB, final QualifiedRelationshipTypeName type )
    {
        return createRelationship( contentA, contentB, type, (DataPath) null );
    }

    private Relationship createRelationship( final ContentId contentA, final ContentId contentB, final QualifiedRelationshipTypeName type,
                                             final DateTime createdTime )
    {
        return createRelationship( contentA, contentB, type, null, createdTime );
    }

    private Relationship createRelationship( final ContentId contentA, final ContentId contentB, final QualifiedRelationshipTypeName type,
                                             final DataPath managingData )
    {
        return createRelationship( contentA, contentB, type, managingData, NOW );
    }

    private Relationship createRelationship( final ContentId contentA, final ContentId contentB, final QualifiedRelationshipTypeName type,
                                             final DataPath managingData, final DateTime createdTime )
    {
        return Relationship.newRelationship().
            type( type ).
            fromContent( contentA ).
            toContent( contentB ).
            managed( managingData ).
            creator( AccountKey.superUser() ).
            createdTime( createdTime ).
            build();
    }

    private Content createContent( String path )
    {
        return newContent().path( ContentPath.from( path ) ).build();
    }

    private Relationships sortOnCreatedTime( Relationships relationships )
    {
        final List<Relationship> sortedList = new ArrayList<>( relationships.getList() );
        Collections.sort( sortedList, new Comparator<Relationship>()
        {
            @Override
            public int compare( final Relationship o1, final Relationship o2 )
            {
                return o1.getCreatedTime().compareTo( o2.getCreatedTime() );
            }
        } );

        return Relationships.from( sortedList );
    }
}
