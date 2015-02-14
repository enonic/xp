package com.enonic.xp.core.impl.module;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.support.SerializingTestHelper;
import com.enonic.xp.xml.XmlException;

public class ModuleXmlBuilderTest
{
    private final SerializingTestHelper serializingTestHelper;

    private final ModuleXmlBuilder xmlBuilder;

    public ModuleXmlBuilderTest()
    {
        this.serializingTestHelper = new SerializingTestHelper( this, true );
        this.xmlBuilder = new ModuleXmlBuilder();
    }

    private String loadTestXml( final String fileName )
    {
        return this.serializingTestHelper.loadTextXml( fileName );
    }

    @Test
    public void testModuleXmlDeserialization()
    {
        final String xml = loadTestXml( "serialized-module.xml" );
        ModuleKey key = ModuleKey.from( "mymodule" );
        final ModuleBuilder module = new ModuleBuilder().
            moduleKey( key ).
            moduleVersion( ModuleVersion.from( "1.0.0" ) );
        this.xmlBuilder.toModule( xml, module, key );
        Assert.assertEquals( createModule().toString(), module.build().toString() );
    }

    @Test(expected = XmlException.class)
    public void testBadXmlModuleSerialization()
    {
        this.xmlBuilder.toModule( "<module><display-name/>", new ModuleBuilder(), ModuleKey.from( "testModuleKey" ) );
    }

    private Module createModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        return new ModuleBuilder().
            moduleKey( ModuleKey.from( "mymodule" ) ).
            displayName( "module display name" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            config( config ).
            metaSteps( MixinNames.from( "system:menu-item", "mymodule:my-meta-mixin" ) ).
            build();
    }
}
