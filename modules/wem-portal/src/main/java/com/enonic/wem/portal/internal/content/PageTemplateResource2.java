package com.enonic.wem.portal.internal.content;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

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
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.internal.base.BaseResource2;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsController;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.site.ModuleConfig.newModuleConfig;

@Path("/theme/{siteTemplateKey}/{pageTemplateKey}")
public final class PageTemplateResource2
    extends BaseResource2
{
    protected JsControllerFactory controllerFactory;

    protected SiteTemplateService siteTemplateService;

    protected PageTemplateService pageTemplateService;

    protected PageDescriptorService pageDescriptorService;

    protected PageTemplateKey pageTemplateKey;

    protected SiteTemplateKey siteTemplateKey;

    @Context
    protected Request request;

    @PathParam("pageTemplateKey")
    public void setPageTemplateKey( final String value )
    {
        this.pageTemplateKey = PageTemplateKey.from( value );
    }

    @PathParam("siteTemplateKey")
    public void setSiteTemplateKey( final String value )
    {
        this.siteTemplateKey = SiteTemplateKey.from( value );
    }

    @GET
    public Response doHandle()
    {
        final PageTemplate pageTemplate = this.pageTemplateService.getByKey( pageTemplateKey, siteTemplateKey );
        final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( siteTemplateKey );

        return renderPageTemplate( siteTemplate, pageTemplate );
    }

    private Response renderPageTemplate( final SiteTemplate siteTemplate, final PageTemplate pageTemplate )
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
        jsRequest.setMethod( this.request.getMethod() );
        context.setRequest( jsRequest );

        final JsController controller = this.controllerFactory.newController( pageDescriptor.getResourceKey() );
        controller.execute( context );

        final RenderResult result = new JsHttpResponseSerializer( context.getResponse() ).serialize();
        return toResponse( result );
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
}
