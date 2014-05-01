package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.site.SiteTemplate
import com.enonic.wem.core.config.SystemConfig
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito
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

        this.service.systemConfig = Mockito.mock( SystemConfig.class );
        this.service.siteTemplateExporter = Mockito.mock( SiteTemplateExporter.class );

        def tempDir = this.folder.newFolder().toPath();
        this.templatesDir = tempDir.resolve( "templates" );
        Files.createDirectory( this.templatesDir );

        Mockito.when( this.service.systemConfig.getTemplatesDir() ).thenReturn( this.templatesDir )
    }

    def SiteTemplate createSiteTemplate( SiteTemplate.Builder siteTemplateBuilder )
    {
        def siteTemplate = siteTemplateBuilder.build();
        def templateDir = this.templatesDir.resolve( siteTemplate.getKey().toString() );
        Files.createDirectories( templateDir );

        Mockito.when( this.service.siteTemplateExporter.importFromDirectory( templateDir ) ).thenReturn( siteTemplateBuilder );
        return siteTemplate;
    }
}
