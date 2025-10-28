package com.enonic.xp.page;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DescriptorsTest
{
    @Test
    void pageDescriptor()
    {
        final Form pageForm = Form.create().
            addFormItem( Input.create().name( "pause" ).label( "pause" ).inputType( InputTypeName.DOUBLE ).build() ).
            // add input of type region
                build();

        final PageDescriptor pageDescriptor = PageDescriptor.create().
            displayName( "Landing page" ).
            config( pageForm ).
            regions( RegionDescriptors.create().build() ).
            key( DescriptorKey.from( "module:landing-page" ) ).
            build();

        assertEquals( "Landing page", pageDescriptor.getDisplayName() );
        assertEquals( "landing-page", pageDescriptor.getName() );
    }

    @Test
    void partDescriptor()
    {
        final Form partForm = Form.create().
            addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypeName.DOUBLE ).build() ).
            build();

        final PartDescriptor partDescriptor = PartDescriptor.create().
            displayName( "News part" ).
            config( partForm ).
            key( DescriptorKey.from( "module:new-part" ) ).
            build();

        final PartDescriptor copy = PartDescriptor.copyOf( partDescriptor ).build();

        assertEquals( "News part", partDescriptor.getDisplayName() );
        assertEquals( partDescriptor.getComponentPath(), copy.getComponentPath() );
    }

    @Test
    void layoutDescriptor()
    {
        final Form layoutForm = Form.create().
            addFormItem( Input.create().name( "columns" ).label( "columns" ).inputType( InputTypeName.DOUBLE ).build() ).
            build();

        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create().
            displayName( "Fancy layout" ).
            config( layoutForm ).
            regions( RegionDescriptors.create().build() ).
            key( DescriptorKey.from( "module:fancy-layout" ) ).
            build();

        final LayoutDescriptor copy = LayoutDescriptor.copyOf( layoutDescriptor ).build();

        assertEquals( "Fancy layout", layoutDescriptor.getDisplayName() );
        assertEquals( layoutForm, layoutDescriptor.getConfig() );
        assertEquals( layoutDescriptor.getComponentPath(), copy.getComponentPath() );
        assertEquals( layoutDescriptor.getRegions(), copy.getRegions() );
    }
}
