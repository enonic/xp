package com.enonic.wem.core.module;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.core.AbstractSerializerTest;

import static org.junit.Assert.*;

public class ModuleXmlSerializerTest
    extends AbstractSerializerTest
{

    private ModuleXmlSerializer moduleSerializer;

    @Before
    public void setUp()
    {
        moduleSerializer = new ModuleXmlSerializer();
    }


    @Test
    public void testModuleXmlDeserialization()
    {

        String xml = loadTestXml( "serialized-module.xml" );
        ModuleBuilder module = new ModuleBuilder().moduleKey( ModuleKey.from( "mymodule-1.0.0" ) );
        moduleSerializer.toModule( xml, module );
        assertEquals( createModule().toString(), module.build().toString() );
    }

    @Test
    public void testXmlModuleSerialization()
    {
        final Module module = createModule();
        final String serializedModule = moduleSerializer.toString( module );
        assertEquals( loadTestXml( "serialized-module.xml" ), serializedModule );
    }

    @Test(expected = XmlParsingException.class)
    public void testBadXmlModuleSerialization()
    {
        moduleSerializer.toModule( "<module><display-name/>", new ModuleBuilder() );
    }

    private Module createModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        return new ModuleBuilder().
            moduleKey( ModuleKey.from( "mymodule-1.0.0" ) ).
            displayName( "module display name" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            config( config ).
            build();
    }
}
