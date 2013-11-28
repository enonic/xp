package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.command.Commands.site;
import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static com.google.common.io.ByteStreams.asByteSource;


public class SitesInitializer
    extends BaseInitializer
{
    private Client client;

    private SiteTemplate siteTemplate;

    protected SitesInitializer()
    {
        super( 13, "sites" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        this.siteTemplate = createSiteTemplate();
        createDefaultSites();
    }

    private void createDefaultSites()
    {
        createSite( "bluman trampoliner", "Bluman Trampoliner" );
        createSite( "bluman intranett", "Bluman Intranett" );
        createSite( "bildearkiv", "Bildearkiv" );
    }

    private void createSite( final String name, final String displayName )
    {
        final ContentId content = createSiteRootContent( name, displayName );

        final CreateSite createSite = site().create().content( content ).template( this.siteTemplate.getKey() );
        final Content siteContent = client.execute( createSite );
    }

    private PageTemplate createPageTemplate( final Module controllerModule )
    {
        final ResourcePath pageTemplateController = ResourcePath.from( "controllers/main-page.js" );
        final ModuleResourceKey pageTemplateDescriptor = new ModuleResourceKey( controllerModule.getModuleKey(), pageTemplateController );

        final PageTemplate pageTemplate = newPageTemplate().
            name( new PageTemplateName( "mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( pageTemplateDescriptor ).
            build();
        return pageTemplate;
    }

    private SiteTemplate createSiteTemplate()
    {
        final Module defaultModule = createDefaultModule();
        final PageTemplate pageTemplate = createPageTemplate( defaultModule );

        final CreateSiteTemplate createSiteTemplate = site().template().create().
            siteTemplateKey( SiteTemplateKey.from( "Blueman-1.0.0" ) ).
            displayName( "Blueman Site Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( defaultModule.getKey() ) ).
            description( "Demo site template" ).
            rootContentType( ContentTypeName.page() ).
            addTemplate( pageTemplate );
        return client.execute( createSiteTemplate );
    }

    private Module createDefaultModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "dummy-param" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final ModuleFileEntry.Builder controllersDir = directoryBuilder( "controllers" ).
            addFile( "main-page.js", asByteSource( "some_code();".getBytes() ) );
        final ModuleFileEntry moduleDirectoryEntry = ModuleFileEntry.directoryBuilder( "" ).
            addEntry( controllersDir ).
            build();

        final CreateModule createModule = Commands.module().create().
            name( "demo-module" ).
            version( ModuleVersion.from( 1, 0, 0 ) ).
            displayName( "Demo module" ).
            info( "For demo purposes only." ).
            url( "http://enonic.net" ).
            vendorName( "Enonic AS" ).
            vendorUrl( "http://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            moduleDependencies( ModuleKeys.empty() ).
            contentTypeDependencies( ContentTypeNames.empty() ).
            config( config ).
            moduleDirectoryEntry( moduleDirectoryEntry );

        return client.execute( createModule );
    }

    private ContentId createSiteRootContent( final String name, final String displayName )
    {
        final CreateContent createContent = Commands.content().create().
            name( name ).
            parent( ContentPath.ROOT ).
            displayName( displayName ).
            contentType( ContentTypeName.page() ).
            form( Form.newForm().build() ).
            contentData( new ContentData() );
        final ContentId contentId = client.execute( createContent ).getContentId();
        return contentId;
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
