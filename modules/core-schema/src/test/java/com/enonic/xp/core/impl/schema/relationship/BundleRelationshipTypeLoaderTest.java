package com.enonic.xp.core.impl.schema.relationship;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.core.impl.schema.AbstractBundleTest;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypes;

import static org.junit.Assert.*;

public class BundleRelationshipTypeLoaderTest
    extends AbstractBundleTest
{
    @Test
    public void test_not_module()
        throws Exception
    {
        startBundles( newBundle( "not-module" ) );

        final Bundle bundle = findBundle( "not-module" );
        assertNotNull( bundle );

        final BundleRelationshipTypeLoader bundleRelationshipTypeLoader = new BundleRelationshipTypeLoader( bundle );
        final RelationshipTypes relationshipTypes = bundleRelationshipTypeLoader.load();
        assertNull( relationshipTypes );
    }

    @Test
    public void test_loaded_relationship_types()
        throws Exception
    {
        startBundles( newBundle( "module2" ) );

        final Bundle bundle = findBundle( "module2" );
        assertNotNull( bundle );

        final BundleRelationshipTypeLoader bundleRelationshipTypeLoader = new BundleRelationshipTypeLoader( bundle );
        final RelationshipTypes relationshipTypes = bundleRelationshipTypeLoader.load();
        assertNotNull( relationshipTypes );
        assertEquals( 1, relationshipTypes.getSize() );

        final RelationshipType type = relationshipTypes.get( 0 );
        assertEquals( "member", type.getName().getLocalName() );
        assertNotNull( type.getIcon() );
    }
}

