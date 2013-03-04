package com.enonic.wem.core.content.schema.relationship;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.content.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.api.content.schema.relationship.editor.SetRelationshipTypeEditor;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.initializer.InitializerTask;

import static com.enonic.wem.api.content.schema.relationship.RelationshipType.newRelationshipType;

@Component
@Order(10)
public class RelationshipTypesInitializer
    implements InitializerTask
{
    private static final RelationshipType DEFAULT =
        createRelationshipType( QualifiedRelationshipTypeName.DEFAULT, "Default", "relates to", "related of" );

    private static final RelationshipType PARENT =
        createRelationshipType( QualifiedRelationshipTypeName.PARENT, "Parent", "parent of", "child of" );

    private static final RelationshipType LINK =
        createRelationshipType( QualifiedRelationshipTypeName.LINK, "Link", "links to", "linked by" );

    private static final RelationshipType LIKE = createRelationshipType( QualifiedRelationshipTypeName.LIKE, "Like", "likes", "liked by" );

    private Client client;

    @Override
    public void initialize()
        throws Exception
    {
        createOrUpdate( DEFAULT );
        createOrUpdate( PARENT );
        createOrUpdate( LINK );
        createOrUpdate( LIKE );
    }

    private void createOrUpdate( final RelationshipType relationshipType )
    {
        final QualifiedRelationshipTypeNames qualifiedNames = QualifiedRelationshipTypeNames.from( relationshipType.getQualifiedName() );
        final boolean notExists = client.execute( Commands.relationshipType().exists().qualifiedNames( qualifiedNames ) ).isEmpty();
        final Icon icon = loadRelationshipTypeIcon( relationshipType.getQualifiedName() );
        if ( notExists )
        {
            final CreateRelationshipType createCommand = Commands.relationshipType().create();
            createCommand.
                name( relationshipType.getName() ).
                displayName( relationshipType.getDisplayName() ).
                module( ModuleName.SYSTEM ).
                fromSemantic( relationshipType.getFromSemantic() ).
                toSemantic( relationshipType.getToSemantic() ).
                icon( icon );
            client.execute( createCommand );
        }
        else
        {
            final UpdateRelationshipType updateCommand = Commands.relationshipType().update();
            updateCommand.selector( relationshipType.getQualifiedName() );
            updateCommand.editor( SetRelationshipTypeEditor.newSetRelationshipTypeEditor().
                displayName( relationshipType.getDisplayName() ).
                fromSemantic( relationshipType.getFromSemantic() ).
                toSemantic( relationshipType.getToSemantic() ).
                allowedFromTypes( relationshipType.getAllowedFromTypes() ).
                allowedToTypes( relationshipType.getAllowedToTypes() ).
                build() );
            client.execute( updateCommand );
        }
    }

    private static Icon loadRelationshipTypeIcon( final QualifiedRelationshipTypeName qualifiedName )
    {
        try
        {
            final String filePath = "/META-INF/relationship-types/" + qualifiedName.toString().replace( ":", "_" ).toLowerCase() + ".png";
            final byte[] iconData = IOUtils.toByteArray( RelationshipTypesInitializer.class.getResourceAsStream( filePath ) );
            return Icon.from( iconData, "image/png" );
        }
        catch ( Exception e )
        {
            return null; // icon for relationship type not found
        }
    }

    private static RelationshipType createRelationshipType( final QualifiedRelationshipTypeName qualifiedName, final String displayName,
                                                            final String fromSemantic, final String toSemantic )
    {
        return newRelationshipType().
            name( qualifiedName.getLocalName() ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic ).
            module( qualifiedName.getModuleName() ).
            icon( loadRelationshipTypeIcon( qualifiedName ) ).
            build();
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
