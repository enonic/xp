package com.enonic.xp.region;

import org.junit.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;

import static com.enonic.xp.region.RegionDescriptors.newRegionDescriptors;
import static org.junit.Assert.*;

public class LayoutDescriptorsTest
{
    @Test
    public void testBuilder()
    {
        final LayoutDescriptors layoutDescriptors = LayoutDescriptors.newLayoutDescriptors().
            add( LayoutDescriptor.create().
                name( "fancy-layout" ).
                displayName( "Fancy layout" ).
                config( Form.newForm().build() ).
                regions( newRegionDescriptors().build() ).
                key( DescriptorKey.from( "module:fancy-layout" ) ).
                build() ).
            add( LayoutDescriptor.create().
                name( "fancy-layout2" ).
                displayName( "Fancy layout2" ).
                config( Form.newForm().build() ).
                regions( newRegionDescriptors().build() ).
                key( DescriptorKey.from( "module:fancy-layout2" ) ).
                build() ).
            build();

        assertEquals( 2, layoutDescriptors.getSize() );
        assertNotNull( layoutDescriptors.getDescriptor( "fancy-layout" ) );
        assertNotNull( layoutDescriptors.getDescriptor( DescriptorKey.from( "module:fancy-layout2" ) ) );
    }

    @Test
    public void empty()
    {
        assertTrue( LayoutDescriptors.empty().isEmpty() );
    }
}
