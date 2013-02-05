package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.exception.RelationshipNotFoundException;


final class RelationshipDaoHandlerUpdate
    extends AbstractRelationshipDaoHandler
{
    private Relationship relationship;

    RelationshipDaoHandlerUpdate( final Session session )
    {
        super( session );
    }

    RelationshipDaoHandlerUpdate relationship( Relationship relationship )
    {
        this.relationship = relationship;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        final Node node = getRelationshipNode( relationship.getId() );
        if ( node == null )
        {
            throw new RelationshipNotFoundException( relationship.getId() );
        }

        final Relationship existing = relationshipJcrMapper.toRelationship( node );
        checkUnchanged( "createdTime", existing.getCreatedTime(), relationship.getCreatedTime() );
        checkUnchanged( "creator", existing.getCreator(), relationship.getCreator() );
        checkUnchanged( "fromContent", existing.getFromContent(), relationship.getFromContent() );
        checkUnchanged( "toContent", existing.getToContent(), relationship.getToContent() );
        checkUnchanged( "type", existing.getType(), relationship.getType() );

        relationshipJcrMapper.toJcr( relationship, node );
    }

    private void checkUnchanged( final String property, final Object previousValue, final Object newValue )
    {
        if ( previousValue == null && newValue == null )
        {
            return;
        }

        if ( previousValue == null )
        {
            throw new IllegalArgumentException( property + " cannot be changed: [" + previousValue + "] -> [" + newValue + "]" );
        }
        else if ( !previousValue.equals( newValue ) )
        {
            throw new IllegalArgumentException( property + " cannot be changed: [" + previousValue + "] -> [" + newValue + "]" );
        }
    }
}
