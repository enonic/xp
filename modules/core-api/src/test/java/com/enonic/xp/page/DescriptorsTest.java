package com.enonic.xp.page;

import org.junit.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.Assert.*;

public class DescriptorsTest
{

    @Test
    public void pageDescriptor()
    {
        final Form pageForm = Form.create().
            addFormItem( Input.create().name( "pause" ).label( "pause" ).inputType( InputTypes.DOUBLE ).build() ).
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
    public void partDescriptor()
    {
        final Form partForm = Form.create().
            addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypes.DOUBLE ).build() ).
            build();

        final PartDescriptor partDescriptor = PartDescriptor.create().
            name( "news-part" ).
            displayName( "News part" ).
            config( partForm ).
            key( DescriptorKey.from( "module:new-part" ) ).
            build();

        final PartDescriptor copy = PartDescriptor.copyOf( partDescriptor ).build();

        assertEquals( "News part", partDescriptor.getDisplayName() );
        assertEquals( partDescriptor.getComponentPath(), copy.getComponentPath() );
    }

    @Test
    public void layoutDescriptor()
    {
        final Form layoutForm = Form.create().
            addFormItem( Input.create().name( "columns" ).label( "columns" ).inputType( InputTypes.DOUBLE ).build() ).
            build();

        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create().
            name( "fancy-layout" ).
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
