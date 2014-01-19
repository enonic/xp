package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.page.part.CreatePartDescriptor;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.page.image.ImageTemplateName;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.command.Commands.page;
import static com.enonic.wem.api.command.Commands.site;
import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.content.page.image.ImageComponent.newImageComponent;
import static com.enonic.wem.api.content.page.part.PartComponent.newPartComponent;
import static com.enonic.wem.api.content.page.part.PartTemplate.newPartTemplate;
import static com.enonic.wem.api.content.page.region.Region.newRegion;
import static com.enonic.wem.api.content.page.region.RegionDescriptor.newRegionDescriptor;
import static com.enonic.wem.api.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.wem.api.content.site.ModuleConfig.newModuleConfig;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static com.enonic.wem.api.form.Form.newForm;
import static com.enonic.wem.api.form.Input.newInput;


public class SitesInitializer
    extends BaseInitializer
{

    private Client client;

    private final static ModuleKey DEMO_MODULE_KEY = ModuleKey.from( "demo-1.0.0" );

    private static final ComponentDescriptorName LANDING_PAGE_DESCRIPTOR_NAME = new ComponentDescriptorName( "landing-page" );

    private static final ComponentDescriptorName PRODUCT_GRID_DESCRIPTOR_NAME = new ComponentDescriptorName( "product-grid" );

    private final static SiteTemplateKey BLUMAN_SITE_TEMPLATE_KEY = SiteTemplateKey.from( "Blueman-1.0.0" );

    private final static SiteTemplateKey BLUMAN_INTRA_SITE_TEMPLATE_KEY = SiteTemplateKey.from( "BluemanIntra-1.0.0" );

    private static final PageTemplateName MAIN_PAGE_PAGE_TEMPLATE_NAME = new PageTemplateName( "main-page" );

    private static final PageTemplateName PRODUCT_GRID_PAGE_TEMPLATE_NAME = new PageTemplateName( "product-grid" );

    private static final PageTemplateName DEPARTMENT_PAGE_PAGE_TEMPLATE_NAME = new PageTemplateName( "department-page" );

    private static final PartTemplateName MY_PART_TEMPLATE_NAME = new PartTemplateName( "my-part-template" );

    private static final ImageTemplateName MY_IMAGE_TEMPLATE_NAME = new ImageTemplateName( "my-image-template" );

    private Module demoModule;

    private PageDescriptor landingPageDescriptor;

    private PartDescriptor myPartDescriptor;

    private PageDescriptor productGridPageDescriptor;

    private PageTemplate mainPageLandingPageTemplate;

    private PageTemplate productGridPageTemplate;

    private PageTemplate departmentPageLandingPageTemplate;

    private PartTemplate myPartTemplate;

    private SiteTemplate bluManTrampolinerSiteTemplate;

    private SiteTemplate bluManIntranettSiteTemplate;

    private Content bluManTrampolineSite;

    private Content bluManIntranetSite;

    protected SitesInitializer()
    {
        super( 13, "sites" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        initializeModules();

        initializeDescriptors();

        initializeSiteTemplates();

        initializeSites();
    }

    private void initializeModules()
    {
        final CreateModule createModule = Commands.module().create().
            name( DEMO_MODULE_KEY.getName().toString() ).
            version( DEMO_MODULE_KEY.getVersion() ).
            displayName( "Demo module" ).
            info( "For demo purposes only." ).
            url( "http://enonic.net" ).
            vendorName( "Enonic AS" ).
            vendorUrl( "http://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            moduleDependencies( ModuleKeys.empty() ).
            contentTypeDependencies( ContentTypeNames.empty() ).
            config( createDemoModuleForm() );

        try
        {
            client.execute( Commands.module().delete().module( DEMO_MODULE_KEY ) );
        }
        catch ( ModuleNotFoundException e )
        {
            // IGNORE IF NOT FOUND
        }

        this.demoModule = client.execute( createModule );
    }

    private void initializeDescriptors()
    {
        landingPageDescriptor = client.execute( page().descriptor().page().create().
            displayName( "Landing page" ).
            name( "landing-page" ).
            key( PageDescriptorKey.from( DEMO_MODULE_KEY, LANDING_PAGE_DESCRIPTOR_NAME ) ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "header" ).build() ).
                add( newRegionDescriptor().name( "main" ).build() ).
                add( newRegionDescriptor().name( "footer" ).build() ).
                build() ).
            config( newForm().
                addFormItem( newInput().name( "background-color" ).label( "Background color" ).inputType( InputTypes.TEXT_LINE ).build() ).
                build() ) );

        productGridPageDescriptor = client.execute( page().descriptor().page().create().
            displayName( "Product grid" ).
            name( "product-grid" ).
            key( PageDescriptorKey.from( DEMO_MODULE_KEY, PRODUCT_GRID_DESCRIPTOR_NAME ) ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "main" ).build() ).
                build() ).
            config( newForm().
                addFormItem( newInput().
                    name( "rows" ).label( "Rows" ).
                    maximumOccurrences( 1 ).minimumOccurrences( 1 ).
                    inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( newInput().
                    name( "columns" ).label( "Columns" ).
                    maximumOccurrences( 1 ).minimumOccurrences( 1 ).
                    inputType( InputTypes.TEXT_LINE ).build() ).
                build() ) );

        final ComponentDescriptorName partName = new ComponentDescriptorName( "mypart" );
        final CreatePartDescriptor createPartDescriptor = page().descriptor().part().create().
            name( partName ).
            key( PartDescriptorKey.from( DEMO_MODULE_KEY, partName ) ).
            displayName( "My part" ).
            config( Form.newForm().build() );
        myPartDescriptor = client.execute( createPartDescriptor );
    }

    private void initializeSiteTemplates()
    {
        myPartTemplate = newPartTemplate().
            key( PartTemplateKey.from( BLUMAN_SITE_TEMPLATE_KEY, DEMO_MODULE_KEY, MY_PART_TEMPLATE_NAME ) ).
            displayName( "My Part" ).
            descriptor( myPartDescriptor.getKey() ).
            build();

        // Main Page
        final PageRegions.Builder mainPageRegions = newPageRegions();
        createDefaultPageRegions( mainPageRegions, BLUMAN_SITE_TEMPLATE_KEY );
        mainPageLandingPageTemplate = newPageTemplate().
            key( PageTemplateKey.from( BLUMAN_SITE_TEMPLATE_KEY, DEMO_MODULE_KEY, MAIN_PAGE_PAGE_TEMPLATE_NAME ) ).
            displayName( "Main Page" ).
            regions( mainPageRegions.build() ).
            config( createLandingPagePageTemplateConfig( "blue" ) ).
            descriptor( landingPageDescriptor.getKey() ).
            build();

        // Product grid
        final PageRegions.Builder productGridRegions;
        productGridRegions = newPageRegions().
            add( newRegion().
                name( "main" ).
                add( PartComponent.newPartComponent().
                    name( "MyComponent" ).
                    template(
                        PartTemplateKey.from( BLUMAN_SITE_TEMPLATE_KEY, DEMO_MODULE_KEY, new PartTemplateName( "my-part-template" ) ) ).
                    build() ).
                build() );
        productGridPageTemplate = newPageTemplate().
            key( PageTemplateKey.from( BLUMAN_SITE_TEMPLATE_KEY, DEMO_MODULE_KEY, PRODUCT_GRID_PAGE_TEMPLATE_NAME ) ).
            displayName( "Product grid" ).
            regions( productGridRegions.build() ).
            config( createProductPageTemplateConfig( 10, 10 ) ).
            descriptor( productGridPageDescriptor.getKey() ).
            build();

        // Department Page
        final PageRegions.Builder departmentPageRegions = newPageRegions();
        createDefaultPageRegions( departmentPageRegions, BLUMAN_INTRA_SITE_TEMPLATE_KEY );
        departmentPageLandingPageTemplate = newPageTemplate().
            key( PageTemplateKey.from( BLUMAN_INTRA_SITE_TEMPLATE_KEY, DEMO_MODULE_KEY, DEPARTMENT_PAGE_PAGE_TEMPLATE_NAME ) ).
            displayName( "Department Page" ).
            regions( departmentPageRegions.build() ).
            config( createLandingPagePageTemplateConfig( "red" ) ).
            descriptor( landingPageDescriptor.getKey() ).
            build();

        try
        {
            client.execute( Commands.site().template().delete( BLUMAN_SITE_TEMPLATE_KEY ) );
        }
        catch ( NoSiteTemplateExistsException e )
        {
            // IGNORE IF NOT FOUND
        }

        try
        {
            client.execute( Commands.site().template().delete( BLUMAN_INTRA_SITE_TEMPLATE_KEY ) );
        }
        catch ( NoSiteTemplateExistsException e )
        {
            // IGNORE IF NOT FOUND
        }

        bluManTrampolinerSiteTemplate = client.execute( site().template().create().
            siteTemplateKey( BLUMAN_SITE_TEMPLATE_KEY ).
            displayName( "Blueman Site Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( demoModule.getKey() ) ).
            description( "Demo site template for portals" ).
            url( "http://enonic.net" ).
            rootContentType( ContentTypeName.page() ).
            addTemplate( mainPageLandingPageTemplate ).
            addTemplate( productGridPageTemplate ).
            addTemplate( myPartTemplate ) );

        bluManIntranettSiteTemplate = client.execute( site().template().create().
            siteTemplateKey( BLUMAN_INTRA_SITE_TEMPLATE_KEY ).
            displayName( "Blueman Intranet Site Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( demoModule.getKey() ) ).
            description( "Demo site template for intranets" ).
            url( "http://enonic.net" ).
            rootContentType( ContentTypeName.page() ).
            addTemplate( departmentPageLandingPageTemplate ) );
    }

    private void initializeSites()
    {
        bluManTrampolineSite = createSiteContent( "bluman-trampoliner", "Bluman Trampoliner" );

        bluManTrampolineSite = client.execute( site().create().
            content( bluManTrampolineSite.getId() ).
            template( this.bluManTrampolinerSiteTemplate.getKey() ).
            moduleConfigs( ModuleConfigs.from( newModuleConfig().
                module( this.demoModule.getModuleKey() ).
                config( createDemoModuleData( "First", "Second" ) ).build() ) ) );

        PageRegions.Builder pageRegions = newPageRegions();
        createDefaultPageRegions( pageRegions, BLUMAN_SITE_TEMPLATE_KEY );

        bluManTrampolineSite = client.execute( page().create().
            content( bluManTrampolineSite.getId() ).
            pageTemplate( this.mainPageLandingPageTemplate.getKey() ).
            regions( pageRegions.build() ).
            config( createLandingPagePageTemplateConfig( "blue" ) ) );

        bluManIntranetSite = createSiteContent( "bluman-intranett", "Bluman Intranett" );

        bluManIntranetSite = client.execute( site().create().
            content( bluManIntranetSite.getId() ).
            template( this.bluManIntranettSiteTemplate.getKey() ).
            moduleConfigs( ModuleConfigs.from( newModuleConfig().
                module( this.demoModule.getModuleKey() ).
                config( createDemoModuleData( "Uno", "Secondo" ) ).build() ) ) );

        pageRegions = newPageRegions();
        createDefaultPageRegions( pageRegions, BLUMAN_INTRA_SITE_TEMPLATE_KEY );
        bluManIntranetSite = client.execute( page().create().
            content( bluManIntranetSite.getId() ).
            pageTemplate( this.departmentPageLandingPageTemplate.getKey() ).
            regions( pageRegions.build() ).
            config( createLandingPagePageTemplateConfig( "red" ) ) );
    }

    private RootDataSet createMyImageTemplateConfig( long width, String caption )
    {
        RootDataSet config = new RootDataSet();
        config.setProperty( "width", new Value.Long( width ) );
        config.setProperty( "caption", new Value.String( caption ) );
        return config;
    }


    private RootDataSet createMyPartTemplateConfig( String text )
    {
        RootDataSet config = new RootDataSet();
        config.setProperty( "text", new Value.String( text ) );
        return config;
    }

    private RootDataSet createLandingPagePageTemplateConfig( final String backgroundColor )
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "background-color", new Value.String( backgroundColor ) );
        return data;
    }

    private RootDataSet createProductPageTemplateConfig( final int rows, final int columns )
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "rows", new Value.String( rows ) );
        data.setProperty( "columns", new Value.String( columns ) );
        return data;
    }

    private void createDefaultPageRegions( final PageRegions.Builder pageRegions, final SiteTemplateKey siteTemplateKey )
    {
        Region myHeaderRegion = newRegion().
            name( "header" ).
            add( newPartComponent().
                name( "PartInHeader" ).
                template( PartTemplateKey.from( siteTemplateKey, DEMO_MODULE_KEY, MY_PART_TEMPLATE_NAME ) ).
                config( createMyImageTemplateConfig( 500, "So sweet!" ) ).
                build() ).
            build();

        Region myMainRegion = newRegion().
            name( "main" ).
            add( newImageComponent().
                name( "FancyImage" ).
                image( ContentId.from( "123" ) ).
                template( ImageTemplateKey.from( siteTemplateKey, DEMO_MODULE_KEY, MY_IMAGE_TEMPLATE_NAME ) ).
                config( createMyImageTemplateConfig( 500, "So nice!" ) ).
                build() ).
            add( newPartComponent().
                name( "PartInMain" ).
                template( PartTemplateKey.from( siteTemplateKey, DEMO_MODULE_KEY, MY_PART_TEMPLATE_NAME ) ).
                config( createMyPartTemplateConfig( "Take a part!" ) ).
                build() ).
            build();

        Region myFooterRegion = newRegion().
            name( "footer" ).
            add( newPartComponent().
                name( "PartInFooter" ).
                template( PartTemplateKey.from( siteTemplateKey, DEMO_MODULE_KEY, MY_PART_TEMPLATE_NAME ) ).
                config( createMyPartTemplateConfig( "Footer" ) ).
                build() ).
            build();

        pageRegions.
            add( myHeaderRegion ).
            add( myMainRegion ).
            add( myFooterRegion ).
            build();
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
        return newForm().
            addFormItem(
                newInput().name( "my-config-a" ).label( "My config A" ).maximumOccurrences( 1 ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem(
                newInput().name( "my-config-b" ).label( "My config B" ).maximumOccurrences( 1 ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
    }

    private Content createSiteContent( final String name, final String displayName )
    {
        final CreateContent createContent = Commands.content().create().
            name( name ).
            parent( ContentPath.ROOT ).
            displayName( displayName ).
            contentType( ContentTypeName.page() ).
            form( newForm().build() ).
            contentData( new ContentData() );
        return client.execute( createContent );
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
