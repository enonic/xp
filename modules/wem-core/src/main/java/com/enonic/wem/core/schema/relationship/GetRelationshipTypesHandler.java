package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.exception.RelationshipTypeNotFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class GetRelationshipTypesHandler
    extends CommandHandler<GetRelationshipTypes>
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final RelationshipTypeNames selectors = command.getNames();
        final RelationshipTypes relationshipTypes;

        if ( command.isGetAll() )
        {
            relationshipTypes = relationshipTypeDao.selectAll( session );
        }
        else
        {
            relationshipTypes = relationshipTypeDao.select( selectors, session );
        }

        if ( relationshipTypes == null )
        {
            throw new RelationshipTypeNotFoundException( selectors );
        }

        command.setResult( relationshipTypes );
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }

}
