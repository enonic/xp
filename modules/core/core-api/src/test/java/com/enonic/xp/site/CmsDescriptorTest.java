package com.enonic.xp.site;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CmsDescriptorTest
{

    @Test
    void testCreateCmsDescriptor()
    {
        //Builds a Form
        final FormItem formItem = Input.create().name( "input" ).label( "Input" ).inputType( InputTypeName.DOUBLE ).build();

        final Form form = Form.create().addFormItem( formItem ).build();

        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        //Builds FragmentsNames
        MixinMappings mixinMappings =
            MixinMappings.from( MixinMapping.create().mixinName( MixinName.from( applicationKey, "my" ) ).build() );

        //Builds a SiteDescriptor
        final CmsDescriptor descriptor =
            CmsDescriptor.create().applicationKey( applicationKey ).form( form ).mixinMappings( mixinMappings ).build();

        assertEquals( form, descriptor.getForm() );
        assertEquals( mixinMappings, descriptor.getMixinMappings() );
    }
}
