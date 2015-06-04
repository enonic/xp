package com.enonic.xp.core.impl.module;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;
import com.enonic.xp.support.SerializingTestHelper;

public class XmlSiteParserTest
{
    private final SerializingTestHelper serializingTestHelper;

    private final XmlSiteParser parser;

    public XmlSiteParserTest()
    {
        this.serializingTestHelper = new SerializingTestHelper( this, true );
        this.parser = new XmlSiteParser();
    }

    private String loadTestXml( final String fileName )
    {
        return this.serializingTestHelper.loadTextXml( fileName );
    }

    @Test
    public void testSiteXmlDeserialization()
    {
        final String xml = loadTestXml( "serialized-site.xml" );

        final ModuleImpl module = new ModuleImpl();
        module.moduleKey = ModuleKey.from( "mymodule" );
        module.moduleVersion = ModuleVersion.from( "1.0.0" );

        this.parser.source( xml );
        this.parser.module( module );
        this.parser.parse();

        Assert.assertEquals( 1, module.config.getFormItems().size() );
        Assert.assertEquals( 2, module.metaSteps.getSize() );
    }

    @Test
    public void testEmptySiteXmlDeserialization()
    {
        final String xml = loadTestXml( "empty-site.xml" );

        final ModuleImpl module = new ModuleImpl();
        module.moduleKey = ModuleKey.from( "mymodule" );
        module.moduleVersion = ModuleVersion.from( "1.0.0" );

        this.parser.source( xml );
        this.parser.module( module );
        this.parser.parse();

        Assert.assertEquals( 0, module.config.getFormItems().size() );
        Assert.assertEquals( 0, module.metaSteps.getSize() );
    }
}
