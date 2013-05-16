package com.enonic.wem.core.content.schema.relationship;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.content.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.content.schema.relationship.RelationshipType.newRelationshipType;
import static com.enonic.wem.api.content.schema.relationship.editor.SetRelationshipTypeEditor.newSetRelationshipTypeEditor;


public class RelationshipTypesInitializer
    extends BaseInitializer
{
    private static final RelationshipType DEFAULT =
        createRelationshipType( QualifiedRelationshipTypeName.DEFAULT, "Default", "relates to", "related of" );

    private static final RelationshipType PARENT =
        createRelationshipType( QualifiedRelationshipTypeName.PARENT, "Parent", "parent of", "child of" );

    private static final RelationshipType LINK =
        createRelationshipType( QualifiedRelationshipTypeName.LINK, "Link", "links to", "linked by" );

    private static final RelationshipType LIKE = createRelationshipType( QualifiedRelationshipTypeName.LIKE, "Like", "likes", "liked by" );

    private static final RelationshipType CITATION =
        createRelationshipType( QualifiedRelationshipTypeName.from( "demo:citation" ), "Citation", "citation in", "cited by",
                                QualifiedContentTypeNames.from( "news:article" ) );

    private static final RelationshipType[] SYSTEM_TYPES = {DEFAULT, PARENT, LINK, LIKE, CITATION};

    protected RelationshipTypesInitializer()
    {
        super( 10, "relationship-types" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        for ( RelationshipType relationshipType : SYSTEM_TYPES )
        {
            relationshipType = RelationshipType.newRelationshipType( relationshipType ).
                icon( loadIcon( relationshipType.getQualifiedName() ) ).
                build();
            createOrUpdate( relationshipType );
        }
    }

    private void createOrUpdate( final RelationshipType relationshipType )
    {
        final QualifiedRelationshipTypeNames qualifiedNames = QualifiedRelationshipTypeNames.from( relationshipType.getQualifiedName() );
        final boolean notExists = client.execute( Commands.relationshipType().exists().qualifiedNames( qualifiedNames ) ).isEmpty();
        if ( notExists )
        {
            final CreateRelationshipType createCommand = Commands.relationshipType().create();
            createCommand.
                name( relationshipType.getName() ).
                module( relationshipType.getModuleName() ).
                displayName( relationshipType.getDisplayName() ).
                fromSemantic( relationshipType.getFromSemantic() ).
                toSemantic( relationshipType.getToSemantic() ).
                allowedFromTypes( relationshipType.getAllowedFromTypes() ).
                allowedToTypes( relationshipType.getAllowedToTypes() ).
                icon( relationshipType.getIcon() );

            client.execute( createCommand );
        }
        else
        {
            final UpdateRelationshipType updateCommand = Commands.relationshipType().update();
            updateCommand.selector( relationshipType.getQualifiedName() );
            updateCommand.editor( newSetRelationshipTypeEditor().
                displayName( relationshipType.getDisplayName() ).
                fromSemantic( relationshipType.getFromSemantic() ).
                toSemantic( relationshipType.getToSemantic() ).
                allowedFromTypes( relationshipType.getAllowedFromTypes() ).
                allowedToTypes( relationshipType.getAllowedToTypes() ).
                icon( relationshipType.getIcon() ).
                build() );
            client.execute( updateCommand );
        }
    }

    private static RelationshipType createRelationshipType( final QualifiedRelationshipTypeName qualifiedName, final String displayName,
                                                            final String fromSemantic, final String toSemantic )
    {
        return createRelationshipType( qualifiedName, displayName, fromSemantic, toSemantic, QualifiedContentTypeNames.empty() );
    }

    private static RelationshipType createRelationshipType( final QualifiedRelationshipTypeName qualifiedName, final String displayName,
                                                            final String fromSemantic, final String toSemantic,
                                                            final QualifiedContentTypeNames toContentTypes )
    {
        return newRelationshipType().
            name( qualifiedName.getLocalName() ).
            module( ModuleName.SYSTEM ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic ).
            module( qualifiedName.getModuleName() ).
            addAllowedToTypes( toContentTypes ).
            build();
    }
}
