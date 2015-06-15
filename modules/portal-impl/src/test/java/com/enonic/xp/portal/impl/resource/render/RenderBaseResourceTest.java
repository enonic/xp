package com.enonic.xp.portal.impl.resource.render;

import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.impl.resource.base.BaseResourceTest;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;

public abstract class RenderBaseResourceTest
    extends BaseResourceTest
{
    protected ContentService contentService;

    protected PageTemplateService pageTemplateService;

    protected PageDescriptorService pageDescriptorService;

    protected ModuleService moduleService;

    protected PortalUrlService portalUrlService;

    @Override
    protected void configure()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.pageTemplateService = Mockito.mock( PageTemplateService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.moduleService = Mockito.mock( ModuleService.class );
        this.portalUrlService = Mockito.mock( PortalUrlService.class );

        this.services.setContentService( this.contentService );
        this.services.setPageTemplateService( this.pageTemplateService );
        this.services.setPageDescriptorService( this.pageDescriptorService );
        this.services.setModuleService( this.moduleService );
        this.services.setPortalUrlService( this.portalUrlService );
    }

    protected final void setupContentAndSite()
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "mymodule:ctype", true );

        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site", "mymodule:contenttypename" ) );

        Mockito.when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
    }

    protected final void setupNonPageContent()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( createPage( "id", "site/somepath/content", "mymodule:ctype", false ) );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site", "mymodule:contenttypename" ) );
    }

    protected final void setupTemplates()
        throws Exception
    {
        Mockito.when( this.pageTemplateService.getByKey( Mockito.eq( PageTemplateKey.from( "my-page" ) ) ) ).thenReturn(
            createPageTemplate() );

        Mockito.when( this.pageDescriptorService.getByKey( Mockito.isA( DescriptorKey.class ) ) ).thenReturn( createDescriptor() );

    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.addString( "property1", "value1" );

        final Content.Builder content = Content.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) );

        if ( withPage )
        {
            PageRegions pageRegions = PageRegions.create().
                add( Region.create().name( "main-region" ).
                    add( PartComponent.create().name( ComponentName.from( "mypart" ) ).
                        build() ).
                    build() ).
                build();

            Page page = Page.create().
                template( PageTemplateKey.from( "my-page" ) ).
                regions( pageRegions ).
                config( rootDataSet ).
                build();
            content.page( page );
        }
        return content.build();
    }

    private Site createSite( final String id, final String path, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private PageTemplate createPageTemplate()
    {
        final PropertyTree pageTemplateConfig = new PropertyTree();
        pageTemplateConfig.addLong( "pause", 10000L );

        PageRegions pageRegions = PageRegions.create().
            add( Region.create().name( "main-region" ).
                add( PartComponent.create().name( ComponentName.from( "mypart" ) ).
                    build() ).
                build() ).
            build();

        final PageTemplate.Builder builder = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "abc" ) ).
            canRender( ContentTypeNames.from( "mymodule:article", "mymodule:banner" ) ).
            regions( pageRegions ).
            config( pageTemplateConfig );

        builder.controller( DescriptorKey.from( "mainmodule:landing-page" ) );

        builder.displayName( "Main page emplate" );
        builder.displayName( "Main page emplate" );
        builder.name( "main-page-template" );
        builder.parentPath( ContentPath.ROOT );

        return builder.build();
    }

    private PageDescriptor createDescriptor()
        throws Exception
    {
        final ModuleKey module = ModuleKey.from( "mainmodule" );
        final String name = "mypage";
        final DescriptorKey key = DescriptorKey.from( module, name );

        final String xml = "<?xml version=\"1.0\"?>\n" +
            "<page>\n" +
            "  <display-name>Landing page</display-name>\n" +
            "  <config/>\n" +
            "</page>";
        final PageDescriptor.Builder builder = PageDescriptor.create();

        parseXml( module, builder, xml );

        return builder.
            key( key ).
            displayName( "Landing page" ).
            build();
    }

    private void parseXml( final ModuleKey moduleKey, final PageDescriptor.Builder builder, final String xml )
    {
        final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();
        parser.builder( builder );
        parser.currentModule( moduleKey );
        parser.source( xml );
        parser.parse();
    }
}
