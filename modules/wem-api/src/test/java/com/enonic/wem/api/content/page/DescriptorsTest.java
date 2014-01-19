package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.wem.api.form.Input.newInput;
import static org.junit.Assert.*;

public class DescriptorsTest
{

    @Test
    public void pageDescriptor()
    {
        Form pageForm = Form.newForm().
            addFormItem( newInput().name( "pause" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            // add input of type region
                build();

        PageDescriptor pageDescriptor = PageDescriptor.newPageDescriptor().
            name( "landing-page" ).
            displayName( "Landing page" ).
            config( pageForm ).
            regions( newRegionDescriptors().build() ).
            key( PageDescriptorKey.from( "module-1.0.0:landing-page" ) ).
            build();

        assertEquals( "Landing page", pageDescriptor.getDisplayName() );
    }

    @Test
    public void partDescriptor()
    {
        Form partForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();

        PartDescriptor partDescriptor = PartDescriptor.newPartDescriptor().
            name( "news-part" ).
            displayName( "News part" ).
            config( partForm ).
            key( PartDescriptorKey.from( "module-1.0.0:new-part" ) ).
            build();

        assertEquals( "News part", partDescriptor.getDisplayName() );
    }

    @Test
    public void layoutDescriptor()
    {
        Form layoutForm = Form.newForm().
            addFormItem( newInput().name( "columns" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();

        LayoutDescriptor layoutDescriptor = LayoutDescriptor.newLayoutDescriptor().
            name( "fancy-layout" ).
            displayName( "Fancy layout" ).
            config( layoutForm ).
            regions( newRegionDescriptors().build() ).
            key( LayoutDescriptorKey.from( "module-1.0.0:fancy-layout" ) ).
            build();

        assertEquals( "Fancy layout", layoutDescriptor.getDisplayName() );
    }

    @Test
    public void imageDescriptor()
    {
        Form partForm = Form.newForm().
            addFormItem( newInput().name( "quality" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();

        ImageDescriptor partDescriptor = ImageDescriptor.newImageDescriptor().
            name( "image" ).
            displayName( "Image" ).
            config( partForm ).
            key( ImageDescriptorKey.from( "module-1.0.0:image" ) ).
            build();

        assertEquals( "Image", partDescriptor.getDisplayName() );
    }

}
