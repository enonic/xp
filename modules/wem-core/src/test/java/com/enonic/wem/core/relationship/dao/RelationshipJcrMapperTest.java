package com.enonic.wem.core.relationship.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.AbstractJcrTest;

import static junit.framework.Assert.assertEquals;

public class RelationshipJcrMapperTest
    extends AbstractJcrTest
{
    private static final DateTime NOW = new DateTime( 2013, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC );

    @Before
    public void before()
    {
        DateTimeUtils.setCurrentMillisFixed( NOW.getMillis() );
    }

    @Override
    protected void setupDao()
        throws Exception
    {
        // no dao to setup
    }

    @Test
    public void toJcr()
        throws RepositoryException
    {
        RelationshipJcrMapper mapper = new RelationshipJcrMapper();

        Node relationshipNode = session.getRootNode().addNode( "relationship" );

        mapper.toJcr( Relationship.newRelationship().
            type( QualifiedRelationshipTypeName.LINK ).
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            managed( DataPath.from( "mySet.myData" ) ).
            property( "stars", "5" ).
            property( "stripes", "3" ).
            creator( AccountKey.superUser() ).
            createdTime( DateTime.now() ).
            build(), relationshipNode );

        assertEquals( AccountKey.superUser().toString(), relationshipNode.getProperty( "creator" ).getString() );
        assertEquals( "2013-01-01T12:00:00.000Z", relationshipNode.getProperty( "createdTime" ).getString() );
        assertEquals( getJsonFileAsJson( "relationship-config.json" ),
                      stringToJson( relationshipNode.getProperty( "relationship" ).getString() ) );
    }

    @Test
    public void toRelationship()
        throws RepositoryException
    {
        // setup
        RelationshipJcrMapper mapper = new RelationshipJcrMapper();

        Node relationshipNode = session.getRootNode().addNode( "relationship" );

        mapper.toJcr( Relationship.newRelationship().
            type( QualifiedRelationshipTypeName.LINK ).
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            creator( AccountKey.superUser() ).
            createdTime( DateTime.now() ).
            build(), relationshipNode );

        // exercise
        Relationship relationship = mapper.toRelationship( relationshipNode );

        // verify
        assertEquals( AccountKey.superUser(), relationship.getCreator() );
        assertEquals( NOW, relationship.getCreatedTime() );
        assertEquals( QualifiedRelationshipTypeName.LINK, relationship.getType() );
        assertEquals( ContentId.from( "111" ), relationship.getFromContent() );
        assertEquals( ContentId.from( "222" ), relationship.getToContent() );
    }
}
