package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleResourceKey;

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
            displayName( "Landing page" ).
            config( pageForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/landing-page.js" ) ).
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
            displayName( "News part" ).
            config( partForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/news-part.js" ) ).
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
            displayName( "Fancy layout" ).
            config( layoutForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/fancy-layout.js" ) ).
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
            displayName( "Image" ).
            config( partForm ).
            build();

        assertEquals( "Image", partDescriptor.getDisplayName() );
    }

}
