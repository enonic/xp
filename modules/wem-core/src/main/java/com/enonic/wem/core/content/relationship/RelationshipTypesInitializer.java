package com.enonic.wem.core.content.relationship;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.content.relationship.UpdateRelationshipTypes;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.command.content.relationship.editor.RelationshipTypeEditors.setRelationshipType;

@Component
@DependsOn("jcrInitializer")
public class RelationshipTypesInitializer
{
    private static final RelationshipType DEFAULT =
        RelationshipType.newRelationshipType().name( "default" ).displayName( "Default" ).fromSemantic( "relates to" ).toSemantic(
            "related by" ).createdTime( new DateTime( 2013, 1, 17, 15, 0, 0 ) ).module( ModuleName.SYSTEM ).build();

    private static final RelationshipType REQUIRE =
        RelationshipType.newRelationshipType().name( "require" ).displayName( "Require" ).fromSemantic( "requires" ).toSemantic(
            "required by" ).createdTime( new DateTime( 2013, 1, 17, 15, 0, 0 ) ).module( ModuleName.SYSTEM ).build();

    private static final RelationshipType LIKE =
        RelationshipType.newRelationshipType().name( "like" ).displayName( "Like" ).fromSemantic( "likes" ).toSemantic(
            "liked by" ).createdTime( new DateTime( 2013, 1, 17, 15, 0, 0 ) ).module( ModuleName.SYSTEM ).build();


    private Client client;

    @PostConstruct
    public void createSystemTypes()
    {
        createOrUpdate( DEFAULT );
        createOrUpdate( REQUIRE );
        createOrUpdate( LIKE );
    }

    private void createOrUpdate( final RelationshipType relationshipType )
    {
        final QualifiedRelationshipTypeNames qualifiedNames = QualifiedRelationshipTypeNames.from( relationshipType.getQualifiedName() );
        final boolean notExists = client.execute( Commands.relationshipType().exists().selectors( qualifiedNames ) ).isEmpty();
        if ( notExists )
        {
            final CreateRelationshipType createCommand = Commands.relationshipType().create();
            createCommand.relationshipType( relationshipType );
            client.execute( createCommand );
        }
        else
        {
            final UpdateRelationshipTypes updateCommand = Commands.relationshipType().update();
            updateCommand.selectors( qualifiedNames );
            updateCommand.editor( setRelationshipType( relationshipType ) );
            client.execute( updateCommand );
        }
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
