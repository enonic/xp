package com.enonic.wem.core.content.site;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
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
        File templatesDir = systemConfig.getTemplatesDir();
        final File templateDir = new File( templatesDir, command.getKey().toString() );
        boolean deleted = false;

        if ( templateDir.exists() )
        {
            FileUtils.deleteDirectory( templateDir );
            deleted = true;
        }
        command.setResult( deleted );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }
}
