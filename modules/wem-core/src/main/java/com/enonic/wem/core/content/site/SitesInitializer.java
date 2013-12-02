package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
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

    private final static SiteTemplateKey BLUMAN_SITE_TEMPLATE_KEY = SiteTemplateKey.from( "Blueman-1.0.0" );

    private Module demoModule;

    private SiteTemplate siteTemplate;

    protected SitesInitializer()
    {
        super( 13, "sites" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        this.demoModule = createDemoModule();
        final PageTemplate pageTemplate = createPageTemplate( this.demoModule );
        this.siteTemplate = createSiteTemplate( BLUMAN_SITE_TEMPLATE_KEY, ModuleKeys.from( this.demoModule.getKey() ), pageTemplate );
        createDefaultSites();
    }

    private void createDefaultSites()
    {
        createSite( "bluman trampoliner", "Bluman Trampoliner" );
        createSite( "bluman intranett", "Bluman Intranett" );
    }

    private void createSite( final String name, final String displayName )
    {
        final ContentId content = createSiteContent( name, displayName );

        final ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( this.demoModule.getModuleKey() ).
            config( createDemoModuleData( "First", "Second" ) ).build();

        final CreateSite createSite = site().create().
            content( content ).
            template( this.siteTemplate.getKey() ).
            moduleConfigs( ModuleConfigs.from( moduleConfig ) );
        client.execute( createSite ).getContent();
    }

    private PageTemplate createPageTemplate( final Module module )
    {
        final ResourcePath pageTemplateController = ResourcePath.from( "controllers/main-page.js" );
        final ModuleResourceKey descriptorModuleResourceKey = new ModuleResourceKey( module.getModuleKey(), pageTemplateController );

        return newPageTemplate().
            name( new PageTemplateName( "mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( descriptorModuleResourceKey ).
            build();
    }

    private SiteTemplate createSiteTemplate( final SiteTemplateKey siteTemplateKey, final ModuleKeys moduleKeys,
                                             final PageTemplate pageTemplate )
    {
        final CreateSiteTemplate createSiteTemplate = site().template().create().
            siteTemplateKey( siteTemplateKey ).
            displayName( "Blueman Site Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( moduleKeys ).
            description( "Demo site template" ).
            url( "http://enonic.net" ).
            rootContentType( ContentTypeName.page() ).
            addTemplate( pageTemplate );
        return client.execute( createSiteTemplate );
    }

    private RootDataSet createDemoModuleData( final String a, final String b )
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "my-config-a", new Value.String( a ) );
        data.setProperty( "my-config-b", new Value.String( b ) );
        return data;
    }

    private Form createDemoModuleForm()
    {
        return Form.newForm().
            addFormItem( Input.newInput().name( "my-config-a" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.newInput().name( "my-config-b" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
    }

    private Module createDemoModule()
    {
        final Form config = createDemoModuleForm();

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

    private ContentId createSiteContent( final String name, final String displayName )
    {
        final CreateContent createContent = Commands.content().create().
            name( name ).
            parent( ContentPath.ROOT ).
            displayName( displayName ).
            contentType( ContentTypeName.page() ).
            form( Form.newForm().build() ).
            contentData( new ContentData() );
        return client.execute( createContent ).getContentId();
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
