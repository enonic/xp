package com.enonic.wem.core.schema.relationship;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.support.BaseCoreSchemaProvider;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;


public class CoreRelationshipTypesProvider
    extends BaseCoreSchemaProvider
    implements SchemaProvider

{
    private static final RelationshipType DEFAULT =
        createRelationshipType( RelationshipTypeName.DEFAULT, "Default", "relates to", "related of" );

    private static final RelationshipType PARENT = createRelationshipType( RelationshipTypeName.PARENT, "Parent", "parent of", "child of" );

    private static final RelationshipType LINK = createRelationshipType( RelationshipTypeName.LINK, "Link", "links to", "linked by" );

    private static final RelationshipType LIKE = createRelationshipType( RelationshipTypeName.LIKE, "Like", "likes", "liked by" );

    private static final RelationshipType CITATION =
        createRelationshipType( RelationshipTypeName.from( "mymodule-1.0.0:citation" ), "Citation", "citation in", "cited by",
                                ContentTypeNames.from( "mymodule-1.0.0:article" ) );

    private static final RelationshipType IMAGE =
        createRelationshipType( RelationshipTypeName.from( "mymodule-1.0.0:image" ), "Image", "relates to image", "related of image",
                                ContentTypeNames.from( "mymodule-1.0.0:image" ) );

    private static final RelationshipType[] SYSTEM_TYPES = {DEFAULT, PARENT, LINK, LIKE, CITATION, IMAGE};


    protected CoreRelationshipTypesProvider()
    {
        super( "relationship-types" );
    }

    @Override
    public Schemas getSchemas()
    {
        List<RelationshipType> relationshipTypes = Lists.newArrayList();
        for ( RelationshipType relationshipType : SYSTEM_TYPES )
        {
            relationshipType = RelationshipType.newRelationshipType( relationshipType ).
                icon( loadSchemaIcon( relationshipType.getName().toString() ) ).
                build();
            relationshipTypes.add( relationshipType );
        }
        return Schemas.from( relationshipTypes );
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
}
