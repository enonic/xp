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
        Form pageTemplateForm = Form.newForm().
            addFormItem( newInput().name( "pause" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            // add input of type region
                build();

        PageDescriptor pageDescriptor = PageDescriptor.newPageDescriptor().
            displayName( "Landing page" ).
            config( pageTemplateForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/landing-page.js" ) ).
            build();

        assertEquals( "Landing page", pageDescriptor.getDisplayName() );
    }

    @Test
    public void partDescriptor()
    {
        Form pageTemplateForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();

        PartDescriptor partDescriptor = PartDescriptor.newPartDescriptor().
            displayName( "News part" ).
            config( pageTemplateForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/news-part.js" ) ).
            build();

        assertEquals( "News part", partDescriptor.getDisplayName() );
    }

    @Test
    public void layoutDescriptor()
    {
        Form pageTemplateForm = Form.newForm().
            addFormItem( newInput().name( "columns" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();

        LayoutDescriptor layoutDescriptor = LayoutDescriptor.newLayoutDescriptor().
            displayName( "Fancy layout" ).
            config( pageTemplateForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/fancy-layout.js" ) ).
            build();

        assertEquals( "Fancy layout", layoutDescriptor.getDisplayName() );
    }
}
