package com.enonic.xp.core.impl.module;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;
import com.enonic.xp.support.SerializingTestHelper;

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
        final ModuleImpl module = new ModuleImpl();
        module.moduleKey = ModuleKey.from( "mymodule" );
        module.moduleVersion = ModuleVersion.from( "1.0.0" );

        this.xmlBuilder.toModule( xml, module );

        Assert.assertNotNull( module.config );
        Assert.assertNotNull( module.metaSteps );
    }
}
