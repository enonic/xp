package com.enonic.xp.schema.impl;

import java.util.List;

import com.enonic.wem.api.schema.relationship.RelationshipTypeProvider;

import static org.junit.Assert.*;

public class SchemaActivatorRelationshipTypeTest
    extends AbstractSchemaActivatorTest
{
    @Override
    protected void validateProviders()
        throws Exception
    {
        final List<RelationshipTypeProvider> list1 = getServices( null, RelationshipTypeProvider.class );
        assertEquals( 1, list1.size() );

        final List<RelationshipTypeProvider> list2 = getServices( "module1", RelationshipTypeProvider.class );
        assertEquals( 0, list2.size() );

        final List<RelationshipTypeProvider> list3 = getServices( "module2", RelationshipTypeProvider.class );
        assertEquals( 1, list3.size() );

        final List<RelationshipTypeProvider> list4 = getServices( "not-module", RelationshipTypeProvider.class );
        assertEquals( 0, list4.size() );
    }

    @Override
    protected void validateNoProviders()
        throws Exception
    {
        final List<RelationshipTypeProvider> list = getServices( null, RelationshipTypeProvider.class );
        assertEquals( 0, list.size() );
    }
}
