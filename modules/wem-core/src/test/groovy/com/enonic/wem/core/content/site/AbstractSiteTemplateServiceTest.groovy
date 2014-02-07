package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.site.SiteTemplate
import com.enonic.wem.core.config.SystemConfig
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

abstract class AbstractSiteTemplateServiceTest
        extends Specification
{
    @Rule
    def TemporaryFolder folder = new TemporaryFolder()

    def SiteTemplateServiceImpl service

    def Path templatesDir

    def setup()
    {
        this.service = new SiteTemplateServiceImpl()

        this.service.systemConfig = Mock( SystemConfig.class );
        this.service.siteTemplateExporter = Mock( SiteTemplateExporter.class );

        def tempDir = this.folder.newFolder().toPath();
        this.templatesDir = tempDir.resolve( "templates" );
        Files.createDirectory( this.templatesDir );

        this.service.systemConfig.getTemplatesDir() >> this.templatesDir
    }

    def SiteTemplate createSiteTemplate( SiteTemplate.Builder siteTemplateBuilder )
    {
        def siteTemplate = siteTemplateBuilder.build();
        def templateDir = this.templatesDir.resolve( siteTemplate.getKey().toString() );
        Files.createDirectories( templateDir );

        this.service.siteTemplateExporter.importFromDirectory( templateDir ) >> siteTemplateBuilder;
        return siteTemplate;
    }
}
