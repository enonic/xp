package com.enonic.wem.api.content.schema.relationshiptype;


import org.junit.Test;

import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;

import static junit.framework.Assert.assertEquals;

public class RelationshipTypeTest
{
    @Test
    public void build()
    {
        // setup
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.module( ModuleName.from( "myModule" ) );
        builder.name( "like" );
        builder.fromSemantic( "likes" );
        builder.toSemantic( "liked by" );
        builder.addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) );
        builder.addAllowedToType( new QualifiedContentTypeName( "myModule:person" ) );

        // exercise
        RelationshipType relationshipType = builder.build();

        // verify
        assertEquals( "like", relationshipType.getName() );
        assertEquals( "myModule", relationshipType.getModuleName().toString() );
        assertEquals( "likes", relationshipType.getFromSemantic() );
        assertEquals( "liked by", relationshipType.getToSemantic() );
        assertEquals( QualifiedContentTypeNames.from( "myModule:person" ), relationshipType.getAllowedFromTypes() );
        assertEquals( QualifiedContentTypeNames.from( "myModule:person" ), relationshipType.getAllowedToTypes() );
    }
}
