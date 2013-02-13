package com.enonic.wem.core.content.relationshiptype;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationshiptype.CreateRelationshipType;
import com.enonic.wem.api.command.content.relationshiptype.UpdateRelationshipTypes;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.initializer.InitializerTask;

import static com.enonic.wem.api.content.relationshiptype.RelationshipType.newRelationshipType;
import static com.enonic.wem.api.content.relationshiptype.editor.RelationshipTypeEditors.setRelationshipType;

@Component
@Order(10)
public class RelationshipTypesInitializer
    implements InitializerTask
{
    private static final RelationshipType PARENT = createRelationshipType( "parent", "Parent", "parent of", "child of" );

    private static final RelationshipType LINK = createRelationshipType( "link", "Link", "links to", "linked by" );

    private static final RelationshipType LIKE = createRelationshipType( "like", "Like", "likes", "liked by" );

    private Client client;

    @Override
    public void initialize()
        throws Exception
    {
        createOrUpdate( PARENT );
        createOrUpdate( LINK );
        createOrUpdate( LIKE );
    }

    private void createOrUpdate( final RelationshipType relationshipType )
    {
        final QualifiedRelationshipTypeNames qualifiedNames = QualifiedRelationshipTypeNames.from( relationshipType.getQualifiedName() );
        final boolean notExists = client.execute( Commands.relationshipType().exists().selectors( qualifiedNames ) ).isEmpty();
        if ( notExists )
        {
            final CreateRelationshipType createCommand = Commands.relationshipType().create();
            createCommand.
                name( relationshipType.getName() ).
                displayName( relationshipType.getDisplayName() ).
                module( ModuleName.SYSTEM ).
                fromSemantic( relationshipType.getFromSemantic() ).
                toSemantic( relationshipType.getToSemantic() ).
                icon( loadRelationshipTypeIcon( relationshipType.getQualifiedName() ) );
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

    private static RelationshipType createRelationshipType( final String name, final String displayName, final String fromSemantic,
                                                            final String toSemantic )
    {
        return newRelationshipType().
            name( name ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic ).
            module( ModuleName.SYSTEM ).
            icon( loadRelationshipTypeIcon( new QualifiedRelationshipTypeName( ModuleName.SYSTEM, name ) ) ).
            build();
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
