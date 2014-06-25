package com.enonic.wem.portal.content;

import org.mockito.Mockito;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.xml.XmlSerializers;
import com.enonic.wem.portal.base.BaseResourceTest;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.page.part.PartComponent.newPartComponent;
import static com.enonic.wem.api.content.page.region.Region.newRegion;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public abstract class RenderBaseResourceTest<T extends RenderBaseResource>
    extends BaseResourceTest<T>
{
    protected ContentService contentService;

    protected SiteService siteService;

    protected SiteTemplateService siteTemplateService;

    protected PageTemplateService pageTemplateService;

    protected PageDescriptorService pageDescriptorService;

    protected JsController jsController;

    @Override
    protected void configure()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.siteService = Mockito.mock( SiteService.class );
        this.pageTemplateService = Mockito.mock( PageTemplateService.class );
        this.siteTemplateService = Mockito.mock( SiteTemplateService.class );
        when( siteTemplateService.getSiteTemplate( any() ) ).thenThrow( new SiteTemplateNotFoundException( null ) );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        final JsControllerFactory jsControllerFactory = Mockito.mock( JsControllerFactory.class );

        this.jsController = Mockito.mock( JsController.class );
        when( jsControllerFactory.newController() ).thenReturn( jsController );

        this.resource.contentService = this.contentService;
        this.resource.siteService = this.siteService;
        this.resource.pageTemplateService = this.pageTemplateService;
        this.resource.siteTemplateService = this.siteTemplateService;
        this.resource.pageDescriptorService = this.pageDescriptorService;
        this.resource.controllerFactory = jsControllerFactory;
    }

    protected final void setupContentAndSite( final Context context )
        throws Exception
    {
        when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ), context ) ).
            thenReturn( createPage( "id", "site/somepath/content", "ctype", true ) );

        when( this.siteService.getNearestSite( isA( ContentId.class ), isA( Context.class ) ) ).
            thenReturn( createSite( "id", "site", "contenttypename" ) );
    }

    protected final void setupNonPageContent( final Context context )
        throws Exception
    {
        when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ), context ) ).
            thenReturn( createPage( "id", "site/somepath/content", "ctype", false ) );

        when( this.siteService.getNearestSite( isA( ContentId.class ), isA( Context.class ) ) ).
            thenReturn( createSite( "id", "site", "contenttypename" ) );
    }

    protected final void setupTemplates()
        throws Exception
    {
        when( this.pageTemplateService.getByKey( Mockito.eq( PageTemplateKey.from( "mymodule|my-page" ) ),
                                                 Mockito.eq( (SiteTemplateKey) null ) ) ).thenReturn( createPageTemplate() );

        when( this.pageDescriptorService.getByKey( isA( PageDescriptorKey.class ) ) ).thenReturn( createDescriptor() );
    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
        rootDataSet.add( dataSet );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "mymodule|my-page" ) ).
            config( rootDataSet ).
            build();

        final Content.Builder content = Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) );
        if ( withPage )
        {
            content.page( page );
        }
        return content.build();
    }

    private Content createSite( final String id, final String path, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
        rootDataSet.add( dataSet );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "mymodule|my-page" ) ).
            config( rootDataSet ).
            build();

        Site site = Site.newSite().build();

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            site( site ).
            build();
    }

    private PageTemplate createPageTemplate()
    {
        final ModuleKey module = ModuleKey.from( "mymodule-1.0.0" );

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", Value.newLong( 10000 ) );

        PageRegions pageRegions = newPageRegions().
            add( newRegion().name( "main-region" ).
                add( newPartComponent().name( ComponentName.from( "mypart" ) ).
                    build() ).
                build() ).
            build();

        return PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( module.getName(), new PageTemplateName( "my-page" ) ) ).
            displayName( "Main page emplate" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule-1.0.0:landing-page" ) ).
            regions( pageRegions ).
            build();
    }

    private PageDescriptor createDescriptor()
        throws Exception
    {
        final ModuleKey module = ModuleKey.from( "mainmodule-1.0.0" );
        final ComponentDescriptorName name = new ComponentDescriptorName( "mypage" );
        final PageDescriptorKey key = PageDescriptorKey.from( module, name );

        final String xml = "<?xml version=\"1.0\"?>\n" +
            "<page-component>\n" +
            "  <display-name>Landing page</display-name>\n" +
            "  <controller>mainmodule-1.0.0:/controller/landing-page.js</controller>\n" +
            "  <config/>\n" +
            "</page-component>";
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();

        XmlSerializers.pageDescriptor().parse( xml ).to( builder );

        return builder.
            key( key ).
            displayName( "Landing page" ).
            build();
    }
}
