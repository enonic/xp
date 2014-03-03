package com.enonic.wem.core.content.page.image

import com.enonic.wem.api.content.page.ComponentDescriptorName
import com.enonic.wem.api.content.page.image.CreateImageDescriptorParams
import com.enonic.wem.api.content.page.image.ImageDescriptorKey
import com.enonic.wem.api.form.Form
import com.enonic.wem.api.form.inputtype.InputTypes
import com.enonic.wem.api.module.ModuleKey

import static com.enonic.wem.api.form.Input.newInput

class ImageDescriptorServiceImpl_createTest
        extends AbstractImageDescriptorServiceTest
{
    def "create image descriptor"()
    {
        given:
        def imageForm = Form.newForm().
                addFormItem( newInput().name( "quality" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
                build();

        def moduleKey = ModuleKey.from( "mainmodule-1.0.0" );
        def descriptorName = new ComponentDescriptorName( "image" );
        def key = ImageDescriptorKey.from( moduleKey, descriptorName );
        def params = new CreateImageDescriptorParams().
                key( key ).
                name( descriptorName ).
                displayName( "Image" ).
                config( imageForm );

        when:
        def result = this.service.create( params );

        then:
        result != null
    }

}
