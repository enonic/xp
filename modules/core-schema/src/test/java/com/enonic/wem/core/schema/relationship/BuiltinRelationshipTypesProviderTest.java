package com.enonic.wem.core.schema.relationship;

import org.junit.Test;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static org.junit.Assert.*;

public class BuiltinRelationshipTypesProviderTest
{
    @Test
    public void testBuiltin()
    {
        final RelationshipTypes types = new BuiltinRelationshipTypesProvider().get();
        assertEquals( 3, types.getSize() );

        assertType( types.get( 0 ), RelationshipTypeName.REFERENCE, true );
        assertType( types.get( 1 ), RelationshipTypeName.PARENT, true );
        assertType( types.get( 2 ), RelationshipTypeName.IMAGE, false );
    }

    private void assertType( final RelationshipType type, final RelationshipTypeName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), type.getName().toString() );
        assertEquals( hasIcon, type.getIcon() != null );
    }
}
