package com.enonic.xp.core.impl.module;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;
import com.enonic.xp.support.SerializingTestHelper;

public class XmlModuleParserTest
{
    private final SerializingTestHelper serializingTestHelper;

    private final XmlModuleParser parser;

    public XmlModuleParserTest()
    {
        this.serializingTestHelper = new SerializingTestHelper( this, true );
        this.parser = new XmlModuleParser();
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

        this.parser.source( xml );
        this.parser.module( module );
        this.parser.parse();

        Assert.assertNotNull( module.config );
        Assert.assertNotNull( module.metaSteps );
    }
}
