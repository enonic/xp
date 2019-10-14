package com.enonic.xp.core.impl.schema.relationship;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypes;

final class BuiltinRelationshipTypes
{
    private static final String RELATIONSHIP_TYPES_FOLDER = "relationship-types";

    private static final RelationshipType REFERENCE =
        createRelationshipType( RelationshipTypeName.REFERENCE, "Reference", "references", "is referenced by" );

    private static final RelationshipType PARENT =
        createRelationshipType( RelationshipTypeName.PARENT, "Parent", "is child of", "is parent of" );

    private static final RelationshipType[] RELATIONSHIP_TYPES = {REFERENCE, PARENT};

    private final RelationshipTypes types;

    public BuiltinRelationshipTypes()
    {
        final List<RelationshipType> relationshipTypeList = generateSystemRelationshipTypes();
        this.types = RelationshipTypes.from( relationshipTypeList );
    }

    public RelationshipTypes getAll()
    {
        return this.types;
    }

    public RelationshipTypes getByApplication( final ApplicationKey key )
    {
        return this.types.filter( ( type ) -> type.getName().getApplicationKey().equals( key ) );
    }

    private static RelationshipType createRelationshipType( final RelationshipTypeName relationshipTypeName, final String displayName,
                                                            final String fromSemantic, final String toSemantic )
    {
        return RelationshipType.create().
            name( relationshipTypeName ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic ).
            addAllowedToTypes( ContentTypeNames.empty() ).
            build();
    }

    private List<RelationshipType> generateSystemRelationshipTypes()
    {
        final List<RelationshipType> relationshipTypes = new ArrayList<>();
        for ( RelationshipType relationshipType : RELATIONSHIP_TYPES )
        {
            relationshipType = RelationshipType.create( relationshipType ).
                icon( loadSchemaIcon( RELATIONSHIP_TYPES_FOLDER, relationshipType.getName().getLocalName() ) ).
                build();
            relationshipTypes.add( relationshipType );
        }
        return relationshipTypes;
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        return SchemaHelper.loadIcon( getClass(), metaInfFolderName, name );
    }
}
