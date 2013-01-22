package com.enonic.wem.core.content.relationship;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationship.GetRelationshipTypes;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipTypeDao;

@Component
public final class GetRelationshipTypesHandler
    extends CommandHandler<GetRelationshipTypes>
{
    private RelationshipTypeDao relationshipTypeDao;

    public GetRelationshipTypesHandler()
    {
        super( GetRelationshipTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final GetRelationshipTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final RelationshipTypes relationshipTypes;
        if ( command.isGetAll() )
        {
            relationshipTypes = relationshipTypeDao.retrieveAllRelationshipTypes( session );
        }
        else
        {
            final QualifiedRelationshipTypeNames relationshipTypeNames = command.getQualifiedNames();
            relationshipTypes = relationshipTypeDao.retrieveRelationshipTypes( relationshipTypeNames, session );
        }
        command.setResult( relationshipTypes );
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }

}
