package com.enonic.wem.api.content.schema.relationship;


import org.junit.Test;

import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;

import static junit.framework.Assert.assertEquals;

public class RelationshipTypeTest
{
    @Test
    public void build()
    {
        // setup
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.module( ModuleName.from( "mymodule" ) );
        builder.name( "like" );
        builder.fromSemantic( "likes" );
        builder.toSemantic( "liked by" );
        builder.addAllowedFromType( new QualifiedContentTypeName( "mymodule:person" ) );
        builder.addAllowedToType( new QualifiedContentTypeName( "mymodule:person" ) );

        // exercise
        RelationshipType relationshipType = builder.build();

        // verify
        assertEquals( "like", relationshipType.getName() );
        assertEquals( "mymodule", relationshipType.getModuleName().toString() );
        assertEquals( "likes", relationshipType.getFromSemantic() );
        assertEquals( "liked by", relationshipType.getToSemantic() );
        assertEquals( QualifiedContentTypeNames.from( "mymodule:person" ), relationshipType.getAllowedFromTypes() );
        assertEquals( QualifiedContentTypeNames.from( "mymodule:person" ), relationshipType.getAllowedToTypes() );
    }
}
