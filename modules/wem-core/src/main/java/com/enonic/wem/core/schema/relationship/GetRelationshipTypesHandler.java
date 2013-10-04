package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class GetRelationshipTypesHandler
    extends CommandHandler<GetRelationshipTypes>
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle( final GetRelationshipTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final RelationshipTypes relationshipTypes;
        if ( command.isGetAll() )
        {
            relationshipTypes = relationshipTypeDao.selectAll( session );
        }
        else
        {
            final QualifiedRelationshipTypeNames selectors = command.getQualifiedNames();
            relationshipTypes = relationshipTypeDao.select( selectors, session );
        }
        command.setResult( relationshipTypes );
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }

}
