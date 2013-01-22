package com.enonic.wem.core.content.relationship;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relation.RelationshipTypesExists;
import com.enonic.wem.api.command.content.relation.RelationshipTypesExistsResult;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipTypeSelector;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipTypeDao;

@Component
public final class RelationshipTypesExistsHandler
    extends CommandHandler<RelationshipTypesExists>
{
    private RelationshipTypeDao relationshipTypeDao;

    public RelationshipTypesExistsHandler()
    {
        super( RelationshipTypesExists.class );
    }

    @Override
    public void handle( final CommandContext context, final RelationshipTypesExists command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final RelationshipTypeSelector selector = command.getSelector();
        final QualifiedRelationshipTypeNames existing = relationshipTypeDao.exists( selector, session );

        command.setResult( RelationshipTypesExistsResult.from( existing ) );
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
