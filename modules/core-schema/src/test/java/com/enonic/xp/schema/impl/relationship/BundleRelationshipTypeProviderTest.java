package com.enonic.xp.schema.impl.relationship;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.xp.schema.impl.AbstractBundleTest;
import com.enonic.xp.schema.impl.relationship.BundleRelationshipTypeProvider;

import static org.junit.Assert.*;

public class BundleRelationshipTypeProviderTest
    extends AbstractBundleTest
{
    @Test
    public void test_not_module()
        throws Exception
    {
        startBundles( newBundle( "not-module" ) );

        final Bundle bundle = findBundle( "not-module" );
        assertNotNull( bundle );

        final BundleRelationshipTypeProvider provider = BundleRelationshipTypeProvider.create( bundle );
        assertNull( provider );
    }

    @Test
    public void test_loaded_mixins()
        throws Exception
    {
        startBundles( newBundle( "module2" ) );

        final Bundle bundle = findBundle( "module2" );
        assertNotNull( bundle );

        final BundleRelationshipTypeProvider provider = BundleRelationshipTypeProvider.create( bundle );
        assertNotNull( provider );

        final RelationshipTypes values = provider.get();
        assertNotNull( values );
        assertEquals( 1, values.getSize() );

        final RelationshipType type = values.get( 0 );
        assertEquals( "member", type.getName().getLocalName() );
        assertNotNull( type.getIcon() );
    }
}

