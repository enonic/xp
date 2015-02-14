package com.enonic.xp.core.impl.schema.relationship;

import org.junit.Test;

import com.enonic.xp.core.schema.relationship.RelationshipType;
import com.enonic.xp.core.schema.relationship.RelationshipTypeName;
import com.enonic.xp.core.schema.relationship.RelationshipTypes;

import static org.junit.Assert.*;

public class BuiltinRelationshipTypesProviderTest
{
    @Test
    public void testBuiltin()
    {
        final RelationshipTypes types = new BuiltinRelationshipTypesProvider().get();
        assertEquals( 2, types.getSize() );

        assertType( types.get( 0 ), RelationshipTypeName.REFERENCE, true );
        assertType( types.get( 1 ), RelationshipTypeName.PARENT, true );
    }

    private void assertType( final RelationshipType type, final RelationshipTypeName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), type.getName().toString() );
        assertEquals( hasIcon, type.getIcon() != null );
    }
}
