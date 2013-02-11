package com.enonic.wem.core.content.relationshiptype;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
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
    private static final RelationshipType DEFAULT = createRelationshipType( "default", "Default", "relates to", "related by" );

    private static final RelationshipType REQUIRE = createRelationshipType( "require", "Require", "requires", "required by" );

    private static final RelationshipType LIKE = createRelationshipType( "like", "Like", "likes", "liked by" );

    private Client client;

    @Override
    public void initialize()
        throws Exception
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
            createdTime( new DateTime( 2013, 1, 17, 15, 0, 0 ) ).
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
