package com.enonic.xp.tools.testing.validate;

import java.io.File;

public final class ModuleValidator
{
    private File rootDir;

    public ModuleValidator rootDir( final File rootDir )
    {
        this.rootDir = rootDir;
        return this;
    }

    public void validate()
    {
        try
        {
            doValidate();
        }
        catch ( final RuntimeException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new AssertionError( e );
        }
    }

    private void doValidate()
        throws Exception
    {
        validateXml();
    }

    private void validateXml()
        throws Exception
    {
        new ModuleXmlValidator( new File( this.rootDir, "module.xml" ) ).validate();
    }
}
