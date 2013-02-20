package com.enonic.wem.core.content.schema.relationshiptype;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.relationshiptype.GetRelationshipTypes;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipTypes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.relationshiptype.dao.RelationshipTypeDao;

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
            relationshipTypes = relationshipTypeDao.selectAll( session );
        }
        else
        {
            final QualifiedRelationshipTypeNames selectors = command.getQualifiedNames();
            relationshipTypes = relationshipTypeDao.select( selectors, session );
        }
        command.setResult( relationshipTypes );
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }

}
