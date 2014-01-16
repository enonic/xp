package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.relationship.GetAllRelationshipTypes;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNotFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class GetAllRelationshipTypesHandler
    extends CommandHandler<GetAllRelationshipTypes>
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final RelationshipTypes relationshipTypes = relationshipTypeDao.selectAll( session );

        if ( relationshipTypes == null )
        {
            throw new RelationshipTypeNotFoundException();
        }

        command.setResult( relationshipTypes );
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }

}
