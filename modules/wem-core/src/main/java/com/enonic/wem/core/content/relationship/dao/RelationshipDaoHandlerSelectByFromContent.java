package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.relationship.Relationships;


final class RelationshipDaoHandlerSelectByFromContent
    extends AbstractRelationshipDaoHandler<Relationships>
{
    private ContentId fromContent;


    RelationshipDaoHandlerSelectByFromContent( final Session session )
    {
        super( session );

    }

    RelationshipDaoHandlerSelectByFromContent fromContent( final ContentId fromContent )
    {
        this.fromContent = fromContent;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        setResult( getRelationships( fromContent ) );
    }
}
