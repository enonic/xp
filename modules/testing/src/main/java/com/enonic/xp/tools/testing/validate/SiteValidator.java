package com.enonic.xp.tools.testing.validate;

import java.io.File;

public final class SiteValidator
{
    private File rootDir;

    public SiteValidator rootDir( final File rootDir )
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
        new SiteXmlValidator( new File( this.rootDir, "site.xml" ) ).validate();
    }
}
