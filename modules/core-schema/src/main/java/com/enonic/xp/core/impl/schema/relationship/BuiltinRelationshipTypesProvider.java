package com.enonic.xp.core.impl.schema.relationship;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeProvider;
import com.enonic.xp.schema.relationship.RelationshipTypes;

import static com.enonic.xp.schema.relationship.RelationshipType.newRelationshipType;

@Component(immediate = true)
public final class BuiltinRelationshipTypesProvider
    implements RelationshipTypeProvider
{
    private static final String RELATIONSHIP_TYPES_FOLDER = "relationship-types";

    // System Relationship Types
    private static final RelationshipType REFERENCE =
        createRelationshipType( RelationshipTypeName.REFERENCE, "Reference", "references", "is referenced by" );

    private static final RelationshipType PARENT =
        createRelationshipType( RelationshipTypeName.PARENT, "Parent", "is child of", "is parent of" );

    private static final RelationshipType[] RELATIONSHIP_TYPES = {REFERENCE, PARENT};

    private final RelationshipTypes types;

    public BuiltinRelationshipTypesProvider()
    {
        this.types = RelationshipTypes.from( generateSystemRelationshipTypes() );
    }

    private static RelationshipType createRelationshipType( final RelationshipTypeName relationshipTypeName, final String displayName,
                                                            final String fromSemantic, final String toSemantic )
    {
        return createRelationshipType( relationshipTypeName, displayName, fromSemantic, toSemantic, ContentTypeNames.empty() );
    }

    private static RelationshipType createRelationshipType( final RelationshipTypeName relationshipTypeName, final String displayName,
                                                            final String fromSemantic, final String toSemantic,
                                                            final ContentTypeNames toContentTypes )
    {
        return newRelationshipType().
            name( relationshipTypeName ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic ).
            addAllowedToTypes( toContentTypes ).
            build();
    }

    private List<RelationshipType> generateSystemRelationshipTypes()
    {
        final List<RelationshipType> relationshipTypes = Lists.newArrayList();
        for ( RelationshipType relationshipType : RELATIONSHIP_TYPES )
        {
            relationshipType = RelationshipType.newRelationshipType( relationshipType ).
                icon( loadSchemaIcon( RELATIONSHIP_TYPES_FOLDER, relationshipType.getName().getLocalName() ) ).
                build();
            relationshipTypes.add( relationshipType );
        }
        return relationshipTypes;
    }

    @Override
    public RelationshipTypes get()
    {
        return this.types;
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name.toLowerCase() + ".png";
        try (final InputStream stream = this.getClass().getResourceAsStream( filePath ))
        {
            if ( stream == null )
            {
                return null;
            }
            return Icon.from( stream, "image/png", Instant.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }
}
