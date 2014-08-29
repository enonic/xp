package com.enonic.wem.portal.internal.content;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.inject.Inject;

import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.internal.base.BaseResource;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsController;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.site.ModuleConfig.newModuleConfig;

public final class PageTemplateResource
    extends BaseResource
{
    @Inject
    protected JsControllerFactory controllerFactory;

    @Inject
    protected SiteTemplateService siteTemplateService;

    @Inject
    protected PageTemplateService pageTemplateService;

    @Inject
    protected PageDescriptorService pageDescriptorService;

    protected String siteTemplateKeyParam;

    protected String pageTemplateKeyParam;


    @Override
    protected void doInit()
        throws ResourceException
    {
        this.pageTemplateKeyParam = attribute( "pageTemplateKey" );
        if ( this.pageTemplateKeyParam == null )
        {
            throw notFound( "Missing pageTemplateKey" );
        }

        this.siteTemplateKeyParam = attribute( "siteTemplateKey" );
        if ( this.siteTemplateKeyParam == null )
        {
            throw notFound( "Missing siteTemplateKey" );
        }
    }

    @Override
    protected Representation doHandle()
        throws ResourceException
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( this.pageTemplateKeyParam );
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( this.siteTemplateKeyParam );
        final PageTemplate pageTemplate = this.pageTemplateService.getByKey( pageTemplateKey, siteTemplateKey );
        final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( siteTemplateKey );

        return renderPageTemplate( siteTemplate, pageTemplate );
    }

    protected Representation renderPageTemplate( final SiteTemplate siteTemplate, final PageTemplate pageTemplate )
        throws ResourceException
    {
        final Content content = createDummyPageContent( siteTemplate );
        final Content siteContent = content;
        final PageDescriptor pageDescriptor = getPageDescriptor( pageTemplate );

        final JsContext context = new JsContext();
        context.setContent( content );
        context.setSiteContent( siteContent );
        context.setPageTemplate( pageTemplate );

        // set resolved module (with version) from site template
        final ModuleKey pageTemplateModule = resolvePageTemplateModule( pageTemplate, siteTemplate );
        context.setResolvedModule( pageTemplateModule.toString() );

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( RenderingMode.EDIT );
        jsRequest.setWorkspace( Workspace.from( "stage" ) );
        jsRequest.setMethod( getRequest().getMethod().toString() );
        context.setRequest( jsRequest );

        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( pageDescriptor.getResourceKey() );
        controller.context( context );
        controller.execute();

        final RenderResult result = new JsHttpResponseSerializer( context.getResponse() ).serialize();
        return toRepresentation( result );
    }

    private ModuleKey resolvePageTemplateModule( final PageTemplate pageTemplate, final SiteTemplate siteTemplate )
    {
        final ModuleName pageTemplateModuleName = pageTemplate.getKey().getModuleName();
        return siteTemplate.getModules().
            stream().
            filter( m -> m.getName().equals( pageTemplateModuleName ) ).
            findFirst().get();
    }

    private Content createDummyPageContent( final SiteTemplate siteTemplate )
    {
        final List<ModuleConfig> moduleConfigList = Lists.newArrayList();
        for ( ModuleKey moduleKey : siteTemplate.getModules() )
        {
            moduleConfigList.add( newModuleConfig().module( moduleKey ).config( new RootDataSet() ).build() );
        }
        final ModuleConfigs moduleConfigs = ModuleConfigs.from( moduleConfigList );
        final Site site = Site.newSite().
            template( siteTemplate.getKey() ).
            moduleConfigs( moduleConfigs ).
            build();

        return newContent().
            parentPath( ContentPath.ROOT ).
            name( "page" ).
            site( site ).
            build();
    }

    private PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final PageDescriptorKey descriptorKey = pageTemplate.getDescriptor();
        final PageDescriptor pageDescriptor = pageDescriptorService.getByKey( descriptorKey );
        if ( pageDescriptor == null )
        {
            throw notFound( "Page descriptor for template [%s] not found", pageTemplate.getName() );
        }

        return pageDescriptor;
    }

    private String attribute( final String name )
    {
        final String value = getAttribute( name );
        try
        {
            return java.net.URLDecoder.decode( value, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            return value;
        }
    }

}
