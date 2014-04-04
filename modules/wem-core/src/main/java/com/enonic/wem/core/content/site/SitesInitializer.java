package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.CreatePageDescriptorParams;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.part.CreatePartDescriptorParams;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.CreateSiteTemplateParam;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.CreateModuleParams;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleService;
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
    private SiteTemplateService siteTemplateService;

    private final static ModuleKey DEMO_MODULE_KEY = ModuleKey.from( "test-1.0.0" );

    private static final ComponentDescriptorName LANDING_PAGE_DESCRIPTOR_NAME = new ComponentDescriptorName( "landing-page" );

    private static final ComponentDescriptorName PRODUCT_GRID_DESCRIPTOR_NAME = new ComponentDescriptorName( "product-grid" );

    private final static SiteTemplateKey HOMEPAGE_SITE_TEMPLATE_KEY = SiteTemplateKey.from( "Homepage-1.0.0" );

    private final static SiteTemplateKey INTRANET_SITE_TEMPLATE_KEY = SiteTemplateKey.from( "Intranet-1.0.0" );

    private static final PageTemplateName MAIN_PAGE_PAGE_TEMPLATE_NAME = new PageTemplateName( "main-page" );

    private static final PageTemplateName PRODUCT_GRID_PAGE_TEMPLATE_NAME = new PageTemplateName( "product-grid" );

    private static final PageTemplateName DEPARTMENT_PAGE_PAGE_TEMPLATE_NAME = new PageTemplateName( "department-page" );

    private Module testModule;

    private PageDescriptor landingPageDescriptor;

    private PartDescriptor myPartDescriptor;

    private PageDescriptor productGridPageDescriptor;

    private PageTemplate mainPageLandingPageTemplate;

    private PageTemplate productGridPageTemplate;

    private PageTemplate departmentPageLandingPageTemplate;

    private SiteTemplate homepageSiteTemplate;

    private SiteTemplate intranetSiteTemplate;

    private Content homepageSite;

    private Content intranetSite;

    @Inject
    protected ModuleService moduleService;

    @Inject
    protected PartDescriptorService partDescriptorService;
    @Inject
    protected PageDescriptorService pageDescriptorService;

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
        final CreateModuleParams params = new CreateModuleParams().
            name( DEMO_MODULE_KEY.getName().toString() ).
            version( DEMO_MODULE_KEY.getVersion() ).
            displayName( "Test module" ).
            info( "For test purposes only." ).
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
            this.moduleService.deleteModule( DEMO_MODULE_KEY );
        }
        catch ( ModuleNotFoundException e )
        {
            // IGNORE IF NOT FOUND
        }

        this.testModule = this.moduleService.createModule( params );
    }

    private void initializeDescriptors()
    {
        CreatePageDescriptorParams params = new CreatePageDescriptorParams().
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
                build() );
        landingPageDescriptor = pageDescriptorService.create( params );

        params = new CreatePageDescriptorParams().
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
                build() );
        productGridPageDescriptor = pageDescriptorService.create( params );

        final ComponentDescriptorName partName = new ComponentDescriptorName( "mypart" );
        final CreatePartDescriptorParams createPartDescriptorParams = new CreatePartDescriptorParams().
            name( partName ).
            key( PartDescriptorKey.from( DEMO_MODULE_KEY, partName ) ).
            displayName( "My part" ).
            config( Form.newForm().build() );
        myPartDescriptor = partDescriptorService.create( createPartDescriptorParams );
    }

    private void initializeSiteTemplates()
    {
        // Main Page
        final PageRegions.Builder mainPageRegions = newPageRegions();
        createDefaultPageRegions( mainPageRegions, HOMEPAGE_SITE_TEMPLATE_KEY );
        mainPageLandingPageTemplate = newPageTemplate().
            key( PageTemplateKey.from( DEMO_MODULE_KEY.getName(), MAIN_PAGE_PAGE_TEMPLATE_NAME ) ).
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
                    descriptor( PartDescriptorKey.from( DEMO_MODULE_KEY, new ComponentDescriptorName( "my-part-template" ) ) ).
                    build() ).
                build() );
        productGridPageTemplate = newPageTemplate().
            key( PageTemplateKey.from( DEMO_MODULE_KEY.getName(), PRODUCT_GRID_PAGE_TEMPLATE_NAME ) ).
            displayName( "Product grid" ).
            regions( productGridRegions.build() ).
            config( createProductPageTemplateConfig( 10, 10 ) ).
            descriptor( productGridPageDescriptor.getKey() ).
            build();

        // Department Page
        final PageRegions.Builder departmentPageRegions = newPageRegions();
        createDefaultPageRegions( departmentPageRegions, INTRANET_SITE_TEMPLATE_KEY );
        departmentPageLandingPageTemplate = newPageTemplate().
            key( PageTemplateKey.from( DEMO_MODULE_KEY.getName(), DEPARTMENT_PAGE_PAGE_TEMPLATE_NAME ) ).
            displayName( "Department Page" ).
            regions( departmentPageRegions.build() ).
            config( createLandingPagePageTemplateConfig( "red" ) ).
            descriptor( landingPageDescriptor.getKey() ).
            build();

        try
        {
            client.execute( Commands.site().template().delete( HOMEPAGE_SITE_TEMPLATE_KEY ) );
        }
        catch ( NoSiteTemplateExistsException e )
        {
            // IGNORE IF NOT FOUND
        }

        try
        {
            client.execute( Commands.site().template().delete( INTRANET_SITE_TEMPLATE_KEY ) );
        }
        catch ( NoSiteTemplateExistsException e )
        {
            // IGNORE IF NOT FOUND
        }

        final CreateSiteTemplateParam paramHomeSite = new CreateSiteTemplateParam().
            siteTemplateKey( HOMEPAGE_SITE_TEMPLATE_KEY ).
            displayName( "Homepage Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( testModule.getKey() ) ).
            description( "Test site template for home pages" ).
            url( "http://enonic.net" ).
            rootContentType( ContentTypeName.page() ).
            addPageTemplate( mainPageLandingPageTemplate ).
            addPageTemplate( productGridPageTemplate );
        homepageSiteTemplate = siteTemplateService.createSiteTemplate( paramHomeSite );

        final CreateSiteTemplateParam paramIntraSite = new CreateSiteTemplateParam().
            siteTemplateKey( INTRANET_SITE_TEMPLATE_KEY ).
            displayName( "Test site template for intranets" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( testModule.getKey() ) ).
            description( "Test site template for intranets ..." ).
            url( "http://enonic.net" ).
            rootContentType( ContentTypeName.page() ).
            addPageTemplate( departmentPageLandingPageTemplate );
        intranetSiteTemplate = siteTemplateService.createSiteTemplate( paramIntraSite );
    }

    private void initializeSites()
    {
        homepageSite = createSiteContent( "homepage", "Homepage" );

        homepageSite = client.execute( site().create().
            content( homepageSite.getId() ).
            template( this.homepageSiteTemplate.getKey() ).
            moduleConfigs( ModuleConfigs.from( newModuleConfig().
                module( this.testModule.getModuleKey() ).
                config( createDemoModuleData( "First", "Second" ) ).build() ) ) );

        PageRegions.Builder pageRegions = newPageRegions();
        createDefaultPageRegions( pageRegions, HOMEPAGE_SITE_TEMPLATE_KEY );

        homepageSite = client.execute( page().create().
            content( homepageSite.getId() ).
            pageTemplate( this.mainPageLandingPageTemplate.getKey() ).
            regions( pageRegions.build() ).
            config( createLandingPagePageTemplateConfig( "blue" ) ) );

        intranetSite = createSiteContent( "intranet", "Intranet" );

        intranetSite = client.execute( site().create().
            content( intranetSite.getId() ).
            template( this.intranetSiteTemplate.getKey() ).
            moduleConfigs( ModuleConfigs.from( newModuleConfig().
                module( this.testModule.getModuleKey() ).
                config( createDemoModuleData( "Uno", "Secondo" ) ).build() ) ) );

        pageRegions = newPageRegions();
        createDefaultPageRegions( pageRegions, INTRANET_SITE_TEMPLATE_KEY );
        intranetSite = client.execute( page().create().
            content( intranetSite.getId() ).
            pageTemplate( this.departmentPageLandingPageTemplate.getKey() ).
            regions( pageRegions.build() ).
            config( createLandingPagePageTemplateConfig( "red" ) ) );
    }

    private RootDataSet createMyImageConfig( long width, String caption )
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
                descriptor( PartDescriptorKey.from( DEMO_MODULE_KEY, new ComponentDescriptorName( "my-part-descriptor" ) ) ).
                config( createMyImageConfig( 500, "So sweet!" ) ).
                build() ).
            build();

        Region myMainRegion = newRegion().
            name( "main" ).
            add( newImageComponent().
                name( "FancyImage" ).
                image( ContentId.from( "123" ) ).
                descriptor( ImageDescriptorKey.from( DEMO_MODULE_KEY, new ComponentDescriptorName( "my-image-descriptor" ) ) ).
                config( createMyImageConfig( 500, "So nice!" ) ).
                build() ).
            add( newPartComponent().
                name( "PartInMain" ).
                descriptor( PartDescriptorKey.from( DEMO_MODULE_KEY, new ComponentDescriptorName( "my-part-descriptor" ) ) ).
                config( createMyPartTemplateConfig( "Take a part!" ) ).
                build() ).
            build();

        Region myFooterRegion = newRegion().
            name( "footer" ).
            add( newPartComponent().
                name( "PartInFooter" ).
                descriptor( PartDescriptorKey.from( DEMO_MODULE_KEY, new ComponentDescriptorName( "my-part-descriptor" ) ) ).
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
    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }
}
