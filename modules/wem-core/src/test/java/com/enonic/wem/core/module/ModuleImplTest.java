package com.enonic.wem.core.module;

import org.junit.Test;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

import static junit.framework.Assert.assertEquals;

public class ModuleImplTest
{
    @Test
    public void testCreateModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final Module module = ModuleBuilder.newModule().
            moduleKey( ModuleKey.from( "mymodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            config( config ).
            build();

        assertEquals( "mymodule-1.0.0", module.getKey().toString() );
        assertEquals( "module display name", module.getDisplayName() );
        assertEquals( "module-info", module.getInfo() );
        assertEquals( "http://enonic.net", module.getUrl() );
        assertEquals( "Enonic", module.getVendorName() );
        assertEquals( "https://www.enonic.com", module.getVendorUrl() );
        assertEquals( InputTypes.TEXT_LINE, module.getConfig().getInput( "some-name" ).getInputType() );
    }
}
