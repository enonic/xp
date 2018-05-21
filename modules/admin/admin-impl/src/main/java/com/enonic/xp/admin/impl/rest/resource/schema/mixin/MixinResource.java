package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Sets;

import com.enonic.xp.admin.impl.json.schema.mixin.MixinJson;
import com.enonic.xp.admin.impl.json.schema.mixin.MixinListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.schema.SchemaImageHelper;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeNameWildcardResolver;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

@Path(ResourceConstants.REST_ROOT + "schema/mixin")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class MixinResource
    implements JaxRsComponent
{
    private static final String DEFAULT_MIME_TYPE = "image/svg+xml";

    private static final SchemaImageHelper HELPER = new SchemaImageHelper();

    private MixinService mixinService;

    private ContentService contentService;

    private SiteService siteService;

    private ContentTypeService contentTypeService;

    private MixinIconUrlResolver mixinIconUrlResolver;

    private MixinIconResolver mixinIconResolver;

    private LocaleService localeService;

    @GET
    public MixinJson get( @QueryParam("name") final String name )
    {
        final MixinName mixinName = MixinName.from( name );
        final Mixin mixin = fetchMixin( mixinName );

        if ( mixin == null )
        {
            throw new WebApplicationException( String.format( "Mixin [%s] was not found.", mixinName ), Response.Status.NOT_FOUND );
        }

        final LocaleMessageResolver localeMessageResolver = new LocaleMessageResolver( this.localeService, mixinName.getApplicationKey() );

        return MixinJson.create().setMixin( mixin ).setIconUrlResolver( this.mixinIconUrlResolver ).setLocaleMessageResolver(
            localeMessageResolver ).build();
    }

    @GET
    @Path("getContentXData")
    public MixinListJson getContentXData( @QueryParam("contentId") final String id )
    {
        final ContentId contentId = ContentId.from( id );
        final Content content = this.contentService.getById( contentId );

        final Set<Mixin> internalXData = Sets.newLinkedHashSet();

        internalXData.addAll( getContentTypeXData( content ).getList() );
        internalXData.addAll( getSiteXData( content ).getList() );

        final Set<Mixin> externalXData = getApplicationXData( content ).getList().
            stream().
            filter( externalMixin -> !internalXData.contains( externalMixin ) ).
            collect( Collectors.toSet() );

        final MixinListJson result = new MixinListJson();
        result.addMixins( createMixinListJson( internalXData ) );
        result.addMixins( createMixinListJson( externalXData, true ) );

        return result;
    }

    private List<MixinJson> createMixinListJson( final Collection<Mixin> xDatas, final Boolean isExternal )
    {
        return xDatas.stream().map(
            mixin -> MixinJson.create().setMixin( mixin ).setIconUrlResolver( this.mixinIconUrlResolver ).setLocaleMessageResolver(
                new LocaleMessageResolver( localeService, mixin.getName().getApplicationKey() ) ).setExternal(
                isExternal ).build() ).collect( Collectors.toList() );
    }

    private List<MixinJson> createMixinListJson( final Collection<Mixin> xDatas )
    {
        return this.createMixinListJson( xDatas, false );
    }

    @GET
    @Path("list")
    public MixinListJson list()
    {
        final Mixins mixins = mixinService.getAll();

        return new MixinListJson( createMixinListJson( mixins.getList() ) );
    }

    @GET
    @Path("byApplication")
    public MixinListJson getByApplication( @QueryParam("applicationKey") final String applicationKey )
    {
        final Mixins mixins = mixinService.getByApplication( ApplicationKey.from( applicationKey ) );

        return new MixinListJson( createMixinListJson( mixins.getList() ) );
    }

    @GET
    @Path("icon/{mixinName}")
    @Produces("image/*")
    public Response getIcon( @PathParam("mixinName") final String mixinNameStr, @QueryParam("size") @DefaultValue("128") final int size,
                             @QueryParam("hash") final String hash )
        throws Exception
    {
        final MixinName mixinName = MixinName.from( mixinNameStr );
        final Icon icon = this.mixinIconResolver.resolveIcon( mixinName );

        final Response.ResponseBuilder responseBuilder;
        if ( icon == null )
        {
            final byte[] defaultMixinImage = HELPER.getDefaultMixinImage();
            responseBuilder = Response.ok( defaultMixinImage, DEFAULT_MIME_TYPE );
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

    private void applyMaxAge( int maxAge, final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( maxAge );
        responseBuilder.cacheControl( cacheControl );
    }

    private Mixin fetchMixin( final MixinName name )
    {
        return mixinService.getByName( name );
    }

    private Mixins getContentTypeXData( final Content content )
    {
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( content.getType() ) );

        return this.mixinService.filterMixinsByContentType( contentType.getMetadata(), contentType.getName(),
                                                            new ContentTypeNameWildcardResolver( this.contentTypeService ) );
    }

    private Mixins getSiteXData( final Content content )
    {
        final Mixins.Builder applicationXDataBuilder = Mixins.create();

        final Site nearestSite = this.contentService.getNearestSite( content.getId() );

        if ( nearestSite != null )
        {
            final List<ApplicationKey> applicationKeys =
                nearestSite.getSiteConfigs().stream().map( SiteConfig::getApplicationKey ).collect( Collectors.toList() );

            final List<SiteDescriptor> siteDescriptors =
                applicationKeys.stream().map( applicationKey -> siteService.getDescriptor( applicationKey ) ).collect(
                    Collectors.toList() );

            siteDescriptors.forEach( siteDescriptor -> applicationXDataBuilder.addAll(
                this.mixinService.filterMixinsByContentType( siteDescriptor.getMetaSteps(), content.getType(),
                                                             new ContentTypeNameWildcardResolver( this.contentTypeService ) ).getList() ) );

        }
        return applicationXDataBuilder.build();
    }

    private Mixins getApplicationXData( final Content content )
    {
        final Site nearestSite = this.contentService.getNearestSite( content.getId() );

        if ( nearestSite != null )
        {
            final List<ApplicationKey> applicationKeys =
                nearestSite.getSiteConfigs().stream().map( SiteConfig::getApplicationKey ).collect( Collectors.toList() );

            final List<MixinName> applicationMixinNames =
                applicationKeys.stream().flatMap( key -> this.mixinService.getByApplication( key ).stream() ).map( Mixin::getName ).collect(
                    Collectors.toList() );

            return this.mixinService.filterMixinsByContentType( MixinNames.from( applicationMixinNames ), content.getType(),
                                                                new ContentTypeNameWildcardResolver( this.contentTypeService ) );

        }

        return Mixins.empty();
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        this.mixinIconResolver = new MixinIconResolver( mixinService );
        this.mixinIconUrlResolver = new MixinIconUrlResolver( this.mixinIconResolver );
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }


    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }


    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}

