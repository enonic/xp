package com.enonic.wem.core.content.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.util.Exceptions;

final class DeleteSiteTemplateCommand
{
    private SiteTemplateKey key;

    private SystemConfig systemConfig;

    public void execute()
    {
        try
        {
            doExecute();
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error deleting site template [{0}]", this.key ).withCause( e );
        }
    }

    private void doExecute()
        throws IOException
    {
        Path templatesDir = systemConfig.getTemplatesDir();
        final Path templateDir = templatesDir.resolve( this.key.toString() );

        if ( Files.isDirectory( templateDir ) )
        {
            FileUtils.deleteDirectory( templateDir.toFile() );
            return;
        }

        throw new SiteTemplateNotFoundException( this.key );
    }

    public DeleteSiteTemplateCommand key( final SiteTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public DeleteSiteTemplateCommand systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }
}
