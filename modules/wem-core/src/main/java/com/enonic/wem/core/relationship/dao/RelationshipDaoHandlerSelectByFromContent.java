package com.enonic.wem.core.relationship.dao;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.core.index.IndexService;


final class RelationshipDaoHandlerSelectByFromContent
    extends AbstractRelationshipDaoHandler<Relationships>
{
    private ContentId fromContent;


    RelationshipDaoHandlerSelectByFromContent( final Session session, final IndexService indexService )
    {
        super( session, indexService );

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
