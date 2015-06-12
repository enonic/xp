package com.enonic.xp.core.impl.schema.relationship;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypes;

final class BuiltinRelationshipTypeLoader
{
    private static final String RELATIONSHIP_TYPES_FOLDER = "relationship-types";

    // System Relationship Types
    private static final RelationshipType REFERENCE =
        createRelationshipType( RelationshipTypeName.REFERENCE, "Reference", "references", "is referenced by" );

    private static final RelationshipType PARENT =
        createRelationshipType( RelationshipTypeName.PARENT, "Parent", "is child of", "is parent of" );

    private static final RelationshipType[] RELATIONSHIP_TYPES = {REFERENCE, PARENT};

    public RelationshipTypes load()
    {
        final List<RelationshipType> relationshipTypeList = generateSystemRelationshipTypes();
        return RelationshipTypes.from( relationshipTypeList );
    }

    private static RelationshipType createRelationshipType( final RelationshipTypeName relationshipTypeName, final String displayName,
                                                            final String fromSemantic, final String toSemantic )
    {
        return RelationshipType.newRelationshipType().
            name( relationshipTypeName ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic ).
            addAllowedToTypes( ContentTypeNames.empty() ).
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
