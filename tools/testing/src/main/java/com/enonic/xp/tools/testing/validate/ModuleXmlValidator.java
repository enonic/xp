package com.enonic.xp.tools.testing.validate;

import java.io.File;

import org.junit.Assert;

final class ModuleXmlValidator
    extends AbstractXmlValidator
{
    private final File file;

    public ModuleXmlValidator( final File file )
    {
        this.file = file;
    }

    public void validate()
        throws Exception
    {
        Assert.assertTrue( "Required module.xml not found", this.file.isFile() );
        validateXml( this.file );
    }
}
