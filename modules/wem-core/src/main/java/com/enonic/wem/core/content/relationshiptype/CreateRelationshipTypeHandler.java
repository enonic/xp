package com.enonic.wem.core.content.relationshiptype;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationship.CreateRelationshipType;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationshiptype.dao.RelationshipTypeDao;

import static com.enonic.wem.api.content.relationshiptype.RelationshipType.newRelationshipType;

@Component
public final class CreateRelationshipTypeHandler
    extends CommandHandler<CreateRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    public CreateRelationshipTypeHandler()
    {
        super( CreateRelationshipType.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateRelationshipType command )
        throws Exception
    {
        final RelationshipType.Builder builder = newRelationshipType();
        builder.name( command.getName() );
        builder.displayName( command.getDisplayName() );
        builder.module( command.getModule() );
        builder.fromSemantic( command.getFromSemantic() );
        builder.toSemantic( command.getToSemantic() );
        builder.addAllowedFromType( command.getAllowedFromTypes() );
        builder.addAllowedToType( command.getAllowedToTypes() );
        builder.createdTime( DateTime.now() );
        builder.modifiedTime( DateTime.now() );
        final RelationshipType relationshipType = builder.build();

        final Session session = context.getJcrSession();
        relationshipTypeDao.create( relationshipType, session );
        session.save();
        command.setResult( relationshipType.getQualifiedName() );
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
