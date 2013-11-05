package com.enonic.wem.core.module;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.AbstractSerializerTest;
import com.enonic.wem.core.support.serializer.XmlParsingException;

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
        Module.Builder module = Module.newModule().moduleKey( ModuleKey.from( "mymodule-1.0.0" ) );
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

    @Test
    public void testXmlModuleDeserializationWithEmptyDeps()
    {
        String xml = loadTestXml( "serialized-module-empty-deps.xml" );
        Module.Builder moduleBuilder = Module.newModule().moduleKey( ModuleKey.from( "mymodule-1.0.0" ) );
        moduleSerializer.toModule( xml, moduleBuilder );
        Module module = moduleBuilder.build();
        assertEquals( module.getModuleDependencies().getSize(), 0 );
        assertEquals( module.getContentTypeDependencies().getSize(), 0 );
    }

    @Test(expected = XmlParsingException.class)
    public void testBadXmlModuleSerialization()
    {
        moduleSerializer.toModule( "<module><display-name/>", Module.newModule() );
    }

    private Module createModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final ContentTypeNames requiredCtypes = ContentTypeNames.from( "ctype1", "ctype2", "ctype3" );
        final ModuleKeys requiredModules = ModuleKeys.from( ModuleKey.from( "modA-1.0.0" ), ModuleKey.from( "modB-1.0.0" ) );
        return Module.newModule().
            moduleKey( ModuleKey.from( "mymodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            addModuleDependency( ModuleKey.from( "modulefoo-1.0.0" ) ).
            addContentTypeDependency( ContentTypeName.from( "article" ) ).
            addModuleDependencies( requiredModules ).
            addContentTypeDependencies( requiredCtypes ).
            config( config ).
            build();
    }
}
