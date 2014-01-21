package com.enonic.wem.core.content.site;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class DeleteSiteTemplateHandler
    extends CommandHandler<DeleteSiteTemplate>
{
    private SystemConfig systemConfig;

    @Override
    public void handle()
        throws Exception
    {
        Path templatesDir = systemConfig.getTemplatesDir();
        final Path templateDir = templatesDir.resolve( command.getKey().toString() );

        if ( Files.isDirectory( templateDir ) )
        {
            FileUtils.deleteDirectory( templateDir.toFile() );
            command.setResult( command.getKey() );
        }
        else
        {
            throw new NoSiteTemplateExistsException( command.getKey() );
        }

    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }
}
