package com.enonic.wem.core.content.relationship.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.core.content.relationship.RelationshipJsonSerializer;
import com.enonic.wem.core.jcr.JcrHelper;

class RelationshipJcrMapper
{
    private static final String RELATIONSHIP = "relationship";

    private final RelationshipJsonSerializer jsonSerializer = new RelationshipJsonSerializer().
        includeCreatedTime( false ).
        includeCreator( false );

    void toJcr( final Relationship relationship, final Node relationshipNode )
        throws RepositoryException
    {
        final String relationshipJson = jsonSerializer.toString( relationship );
        relationshipNode.setProperty( RELATIONSHIP, relationshipJson );
        JcrHelper.setPropertyDateTime( relationshipNode, "createdTime", relationship.getCreatedTime() );
        relationshipNode.setProperty( "creator", relationship.getCreator().toString() );
        JcrHelper.setPropertyDateTime( relationshipNode, "modifiedTime", relationship.getModifiedTime() );
        relationshipNode.setProperty( "modifier", relationship.getModifier().toString() );
    }

    Relationship toRelationship( final Node relationshipNode )
        throws RepositoryException
    {
        final String relationshipJson = relationshipNode.getProperty( RELATIONSHIP ).getString();
        final Relationship relationship = jsonSerializer.toRelationship( relationshipJson );
        return Relationship.newRelationship( relationship ).
            id( RelationshipIdFactory.from( relationshipNode ) ).
            creator( AccountKey.from( relationshipNode.getProperty( "creator" ).getString() ).asUser() ).
            createdTime( JcrHelper.getPropertyDateTime( relationshipNode, "createdTime" ) ).
            modifier( AccountKey.from( relationshipNode.getProperty( "modifier" ).getString() ).asUser() ).
            modifiedTime( JcrHelper.getPropertyDateTime( relationshipNode, "modifiedTime" ) ).
            build();
    }
}
