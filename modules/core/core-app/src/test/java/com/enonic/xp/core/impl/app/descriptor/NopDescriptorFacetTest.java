package com.enonic.xp.core.impl.app.descriptor;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NopDescriptorFacetTest
{
    @Test
    public void testAccessors()
    {
        final NopDescriptorFacet<MyDescriptor> facet = new NopDescriptorFacet<>();
        assertEquals( 0, facet.getAll().getSize() );
        assertEquals( 0, facet.get( ApplicationKeys.empty() ).getSize() );
        assertEquals( 0, facet.get( DescriptorKeys.empty() ).getSize() );
        assertEquals( 0, facet.findAll().getSize() );
        assertEquals( 0, facet.find( ApplicationKeys.empty() ).getSize() );
        assertNull( facet.get( DescriptorKey.from( "app:abc" ) ) );
    }
}
