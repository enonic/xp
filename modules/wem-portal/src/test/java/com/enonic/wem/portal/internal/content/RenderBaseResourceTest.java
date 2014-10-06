package com.enonic.wem.portal.internal.content;

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
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;
import com.enonic.wem.api.xml.mapper.XmlPageDescriptorMapper;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;
import com.enonic.wem.api.xml.serializer.XmlSerializers2;
import com.enonic.wem.portal.internal.base.BaseResourceTest;
import com.enonic.wem.portal.internal.controller.JsController;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;

public abstract class RenderBaseResourceTest<T extends RenderBaseResourceProvider>
    extends BaseResourceTest
{
    protected ContentService contentService;

    protected PageTemplateService pageTemplateService;

    protected PageDescriptorService pageDescriptorService;

    protected JsController jsController;

    protected T resourceProvider;

    protected PostProcessor postProcessor;

    protected ModuleService moduleService;

    public final Workspace testWorkspace = Workspace.from( "test" );

    protected final Context testContext = Context.create().
        workspace( testWorkspace ).
        repository( Repository.create().
            id( RepositoryId.from( "testing" ) ).
            workspaces( Workspaces.from( testWorkspace ) ).
            build() ).
        build();

    @Override
    protected void configure()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.pageTemplateService = Mockito.mock( PageTemplateService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.postProcessor = Mockito.mock( PostProcessor.class );
        this.moduleService = Mockito.mock( ModuleService.class );
        final JsControllerFactory jsControllerFactory = Mockito.mock( JsControllerFactory.class );

        this.jsController = Mockito.mock( JsController.class );
        Mockito.when( jsControllerFactory.newController( Mockito.any() ) ).thenReturn( jsController );

        this.resourceProvider.setContentService( this.contentService );
        this.resourceProvider.setPageTemplateService( this.pageTemplateService );
        this.resourceProvider.setPageDescriptorService( this.pageDescriptorService );
        this.resourceProvider.setControllerFactory( jsControllerFactory );
        this.resourceProvider.setPostProcessor( this.postProcessor );
        this.resourceProvider.setModuleService( this.moduleService );

        this.resources.add( this.resourceProvider );
    }

    protected final void setupContentAndSite( final Context context )
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "mymodule:ctype", true );

        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ), context ) ).
            thenReturn( content );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ), Mockito.isA( Context.class ) ) ).
            thenReturn( createSite( "id", "site", "mymodule:contenttypename" ) );

        Mockito.when( this.contentService.getById( content.getId(), context ) ).
            thenReturn( content );
    }

    protected final void setupNonPageContent( final Context context )
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ), context ) ).
            thenReturn( createPage( "id", "site/somepath/content", "mymodule:ctype", false ) );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ), Mockito.isA( Context.class ) ) ).
            thenReturn( createSite( "id", "site", "mymodule:contenttypename" ) );
    }

    protected final void setupTemplates( final Context context )
        throws Exception
    {
        setupTemplates( context, true );
    }

    protected final void setupTemplates( final Context context, final boolean includeDescriptor )
        throws Exception
    {
        Mockito.when(
            this.pageTemplateService.getByKey( Mockito.eq( PageTemplateKey.from( "my-page" ) ), Mockito.eq( context ) ) ).thenReturn(
            createPageTemplate( includeDescriptor ) );

        if ( includeDescriptor )
        {
            Mockito.when( this.pageDescriptorService.getByKey( Mockito.isA( PageDescriptorKey.class ) ) ).thenReturn( createDescriptor() );
        }
    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
        rootDataSet.add( dataSet );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "my-page" ) ).
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

    private Site createSite( final String id, final String path, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
        rootDataSet.add( dataSet );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.newSite().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private PageTemplate createPageTemplate( final boolean includeDescriptor )
    {
        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", Value.newLong( 10000 ) );

        PageRegions pageRegions = PageRegions.newPageRegions().
            add( Region.newRegion().name( "main-region" ).
                add( PartComponent.newPartComponent().name( ComponentName.from( "mypart" ) ).
                    build() ).
                build() ).
            build();

        final PageTemplate.Builder builder = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "abc" ) ).
            canRender( ContentTypeNames.from( "mymodule:article", "mymodule:banner" ) ).
            regions( pageRegions ).
            config( pageTemplateConfig );

        if ( includeDescriptor )
        {
            builder.descriptor( PageDescriptorKey.from( "mainmodule:landing-page" ) );
        }

        builder.displayName( "Main page emplate" );
        builder.name( "main-page-template" );
        builder.parentPath( ContentPath.ROOT );

        return builder.build();
    }

    private PageDescriptor createDescriptor()
        throws Exception
    {
        final ModuleKey module = ModuleKey.from( "mainmodule" );
        final ComponentDescriptorName name = new ComponentDescriptorName( "mypage" );
        final PageDescriptorKey key = PageDescriptorKey.from( module, name );

        final String xml = "<?xml version=\"1.0\"?>\n" +
            "<page-component>\n" +
            "  <display-name>Landing page</display-name>\n" +
            "  <config/>\n" +
            "</page-component>";
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();

        final XmlPageDescriptor xmlObject = XmlSerializers2.pageDescriptor().parse( xml );
        XmlPageDescriptorMapper.fromXml( xmlObject, builder );

        return builder.
            key( key ).
            displayName( "Landing page" ).
            build();
    }
}
