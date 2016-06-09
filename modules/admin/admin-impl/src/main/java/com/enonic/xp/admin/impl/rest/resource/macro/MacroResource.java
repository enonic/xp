package com.enonic.xp.admin.impl.rest.resource.macro;

import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.macro.json.ApplicationKeysParam;
import com.enonic.xp.admin.impl.rest.resource.macro.json.MacrosJson;
import com.enonic.xp.admin.impl.rest.resource.macro.json.PreviewMacroJson;
import com.enonic.xp.admin.impl.rest.resource.macro.json.PreviewMacroResultJson;
import com.enonic.xp.admin.impl.rest.resource.macro.json.PreviewMacroStringResultJson;
import com.enonic.xp.admin.impl.rest.resource.macro.json.PreviewStringMacroJson;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorScriptFactory;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Path(ResourceConstants.REST_ROOT + "macro")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class MacroResource
    implements JaxRsComponent
{

    private MacroDescriptorService macroDescriptorService;

    private MacroIconResolver macroIconResolver;

    private MacroIconUrlResolver macroIconUrlResolver;

    private MacroProcessorScriptFactory macroProcessorScriptFactory;

    private PortalUrlService portalUrlService;

    private static final MacroImageHelper HELPER = new MacroImageHelper();

    private static final String DEFAULT_MIME_TYPE = "image/svg+xml";

    @POST
    @Path("getByApps")
    public MacrosJson getMacrosByApps( final ApplicationKeysParam appKeys )
    {
        final Set<ApplicationKey> keys = appKeys.getKeys();
        keys.add( ApplicationKey.SYSTEM );
        return new MacrosJson( this.macroDescriptorService.getByApplications( ApplicationKeys.from( keys ) ), this.macroIconUrlResolver );
    }

    @GET
    @Path("icon/{macroKey}")
    @Produces("image/*")
    public Response getIcon( @PathParam("macroKey") final String macroKeyStr, @QueryParam("size") @DefaultValue("128") final int size,
                             @QueryParam("hash") final String hash )
        throws Exception
    {
        final MacroKey macroKey = MacroKey.from( macroKeyStr );
        final Icon icon = this.macroIconResolver.resolveIcon( macroKey );

        final Response.ResponseBuilder responseBuilder;
        if ( icon == null )
        {
            final byte[] defaultMacroImage = HELPER.getDefaultMacroImage();
            responseBuilder = Response.ok( defaultMacroImage, DEFAULT_MIME_TYPE );
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        }
        else
        {
            final Object image = HELPER.isSvg( icon ) ? icon.toByteArray() : HELPER.resizeImage( icon.asInputStream(), size );
            responseBuilder = Response.ok( image, icon.getMimeType() );
            if ( StringUtils.isNotEmpty( hash ) )
            {
                applyMaxAge( Integer.MAX_VALUE, responseBuilder );
            }
        }

        return responseBuilder.build();
    }

    @POST
    @Path("preview")
    public PreviewMacroResultJson macroPreview( @Context HttpServletRequest httpRequest, final PreviewMacroJson previewMacroJson )
    {
        final MacroKey macroKey = previewMacroJson.getMacroKey();
        final MacroDescriptor macroDescriptor = this.macroDescriptorService.getByKey( macroKey );
        if ( macroDescriptor == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }
        final MacroProcessor macroProcessor = macroProcessorScriptFactory.fromScript( macroDescriptor.toControllerResourceKey() );
        if ( macroProcessor == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        final PortalRequest portalRequest = createPortalRequest( httpRequest, previewMacroJson.getContentPath() );
        portalRequest.setContentPath( previewMacroJson.getContentPath() );

        final MacroContext context = createMacroContext( macroDescriptor, previewMacroJson.getFormData(), portalRequest );

        final PortalResponse response = macroProcessor.process( context );
        final Macro macro = createMacro( macroDescriptor, previewMacroJson.getFormData() );
        return new PreviewMacroResultJson( macro, response );
    }

    @POST
    @Path("previewString")
    public PreviewMacroStringResultJson macroPreviewString( final PreviewStringMacroJson previewStringMacroJson )
    {
        final MacroKey macroKey = previewStringMacroJson.getMacroKey();
        final MacroDescriptor macroDescriptor = this.macroDescriptorService.getByKey( macroKey );
        if ( macroDescriptor == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        final Macro macro = createMacro( macroDescriptor, previewStringMacroJson.getFormData() );
        return new PreviewMacroStringResultJson( macro );
    }

    private PortalRequest createPortalRequest( final HttpServletRequest req, final ContentPath contentPath )
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setRawRequest( req );
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/portal" );
        portalRequest.setMode( RenderMode.EDIT );
        portalRequest.setBranch( ContentConstants.BRANCH_DRAFT );
        portalRequest.setScheme( ServletRequestUrlHelper.getScheme( req ) );
        portalRequest.setHost( ServletRequestUrlHelper.getHost( req ) );
        portalRequest.setPort( ServletRequestUrlHelper.getPort( req ) );
        portalRequest.setRemoteAddress( ServletRequestUrlHelper.getRemoteAddress( req ) );
        final PageUrlParams pageUrlParams = new PageUrlParams().portalRequest( portalRequest ).path( contentPath.toString() );
        portalRequest.setPath( portalUrlService.pageUrl( pageUrlParams ) );
        return portalRequest;
    }

    private MacroContext createMacroContext( final MacroDescriptor macroDescriptor, final PropertyTree formData,
                                             final PortalRequest portalRequest )
    {
        final MacroContext.Builder context = MacroContext.create().name( macroDescriptor.getName() );
        final String body = Strings.nullToEmpty( formData.getString( "body" ) );
        context.body( body );
        for ( Property prop : formData.getProperties() )
        {
            if ( !"body".equals( prop.getName() ) && prop.hasNotNullValue() )
            {
                context.param( prop.getName(), prop.getValue().asString() );
            }
        }
        context.request( portalRequest );
        return context.build();
    }

    private Macro createMacro( final MacroDescriptor macroDescriptor, final PropertyTree formData )
    {
        final Macro.Builder context = Macro.create().name( macroDescriptor.getName() );
        final String body = Strings.nullToEmpty( formData.getString( "body" ) );
        context.body( body );
        for ( Property prop : formData.getProperties() )
        {
            if ( !"body".equals( prop.getName() ) && prop.hasNotNullValue() )
            {
                context.param( prop.getName(), prop.getValue().asString() );
            }
        }
        return context.build();
    }

    private void applyMaxAge( int maxAge, final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( maxAge );
        responseBuilder.cacheControl( cacheControl );
    }

    @Reference
    public void setMacroDescriptorService( final MacroDescriptorService macroDescriptorService )
    {
        this.macroDescriptorService = macroDescriptorService;
        this.macroIconResolver = new MacroIconResolver( this.macroDescriptorService );
        this.macroIconUrlResolver = new MacroIconUrlResolver( this.macroIconResolver );
    }

    @Reference
    public void setMacroProcessorScriptFactory( final MacroProcessorScriptFactory macroProcessorScriptFactory )
    {
        this.macroProcessorScriptFactory = macroProcessorScriptFactory;
    }

    @Reference
    public void setPortalUrlService( final PortalUrlService portalUrlService )
    {
        this.portalUrlService = portalUrlService;
    }
}
