package com.enonic.xp.content.page;

import org.junit.Test;

import com.enonic.xp.content.page.region.LayoutDescriptor;
import com.enonic.xp.content.page.region.PartDescriptor;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.inputtype.InputTypes;

import static com.enonic.xp.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.xp.form.Input.newInput;
import static org.junit.Assert.*;

public class DescriptorsTest
{

    @Test
    public void pageDescriptor()
    {
        final Form pageForm = Form.newForm().
            addFormItem( newInput().name( "pause" ).inputType( InputTypes.DOUBLE ).build() ).
            // add input of type region
                build();

        final PageDescriptor pageDescriptor = PageDescriptor.create().
            displayName( "Landing page" ).
            config( pageForm ).
            regions( newRegionDescriptors().build() ).
            key( DescriptorKey.from( "module:landing-page" ) ).
            build();

        assertEquals( "Landing page", pageDescriptor.getDisplayName() );
        assertEquals( "landing-page", pageDescriptor.getName() );
    }

    @Test
    public void partDescriptor()
    {
        final Form partForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( InputTypes.DOUBLE ).build() ).
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
        final Form layoutForm = Form.newForm().
            addFormItem( newInput().name( "columns" ).inputType( InputTypes.DOUBLE ).build() ).
            build();

        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create().
            name( "fancy-layout" ).
            displayName( "Fancy layout" ).
            config( layoutForm ).
            regions( newRegionDescriptors().build() ).
            key( DescriptorKey.from( "module:fancy-layout" ) ).
            build();

        final LayoutDescriptor copy = LayoutDescriptor.copyOf( layoutDescriptor ).build();

        assertEquals( "Fancy layout", layoutDescriptor.getDisplayName() );
        assertEquals( layoutForm, layoutDescriptor.getConfig() );
        assertEquals( layoutDescriptor.getComponentPath(), copy.getComponentPath() );
        assertEquals( layoutDescriptor.getRegions(), copy.getRegions() );

    }

}
