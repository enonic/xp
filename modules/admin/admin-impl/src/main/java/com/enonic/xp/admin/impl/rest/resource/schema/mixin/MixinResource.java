package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.enonic.xp.admin.impl.json.schema.xdata.XDataJson;
import com.enonic.xp.admin.impl.json.schema.xdata.XDataListJson;
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
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static java.util.stream.Collectors.toList;

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

    private XDataService xDataService;

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
    @Path("getApplicationXDataForContentType")
    public XDataListJson getApplicationXDataForContentType( @QueryParam("contentTypeName") final String contentTypeName,
                                                            @QueryParam("applicationKey") final String key )
    {
        final ContentTypeName contentType = ContentTypeName.from( contentTypeName );
        final ApplicationKey applicationKey = ApplicationKey.from( key );

        final SiteDescriptor siteDescriptor = siteService.getDescriptor( applicationKey );

        final XDatas siteXData = this.filterXDatasByContentType( siteDescriptor.getMetaSteps(), contentType );

        final XDatas applicationXData =
            XDatas.from( this.filterXDatasByContentType( this.xDataService.getByApplication( applicationKey ), contentType ).
                stream().filter( externalMixin -> !siteXData.contains( externalMixin ) ).iterator() );

        final XDataListJson result = new XDataListJson();
        result.addXDatas( createXDataListJson( siteXData.getList(), false ) );
        result.addXDatas( createXDataListJson( applicationXData.getList(), true ) );

        return result;
    }

    @GET
    @Path("getContentXData")
    public XDataListJson getContentXData( @QueryParam("contentId") final String id )
    {
        final ContentId contentId = ContentId.from( id );
        final Content content = this.contentService.getById( contentId );

        final Set<XData> internalXData = Sets.newLinkedHashSet();

        internalXData.addAll( getContentTypeXData( content ).getList() );
        internalXData.addAll( getSiteXData( content ).getList() );

        final Set<XData> externalXData = getApplicationXData( content ).
            stream().
            filter( externalMixin -> !internalXData.contains( externalMixin ) ).
            collect( Collectors.toSet() );

        final XDataListJson result = new XDataListJson();
        result.addXDatas( createXDataListJson( internalXData, false ) );
        result.addXDatas( createXDataListJson( externalXData, true ) );

        return result;
    }

    private List<XDataJson> createXDataListJson( final Collection<XData> xDatas, final Boolean isExternal )
    {
        return xDatas.stream().map(
            xData -> XDataJson.create().setXData( xData ).setIconUrlResolver( this.mixinIconUrlResolver ).setLocaleMessageResolver(
                new LocaleMessageResolver( localeService, xData.getName().getApplicationKey() ) ).setExternal(
                isExternal ).build() ).collect( toList() );
    }

    private List<MixinJson> createMixinListJson( final Collection<Mixin> mixins )
    {
        return mixins.stream().map(
            mixin -> MixinJson.create().setMixin( mixin ).setIconUrlResolver( this.mixinIconUrlResolver ).setLocaleMessageResolver(
                new LocaleMessageResolver( localeService, mixin.getName().getApplicationKey() ) ).setExternal( false ).build() ).collect(
            toList() );
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

    private XDatas getContentTypeXData( final Content content )
    {
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( content.getType() ) );

        return XDatas.from( this.filterXDatasByContentType( contentType.getMetadata(), contentType.getName() ) );
    }

    private XDatas getSiteXData( final Content content )
    {
        final XDatas.Builder applicationXDataBuilder = XDatas.create();

        final Site nearestSite = this.contentService.getNearestSite( content.getId() );

        if ( nearestSite != null )
        {
            final List<ApplicationKey> applicationKeys =
                nearestSite.getSiteConfigs().stream().map( SiteConfig::getApplicationKey ).collect( toList() );

            final List<SiteDescriptor> siteDescriptors =
                applicationKeys.stream().map( applicationKey -> siteService.getDescriptor( applicationKey ) ).collect( toList() );

            siteDescriptors.forEach( siteDescriptor -> applicationXDataBuilder.addAll(
                XDatas.from( this.filterXDatasByContentType( siteDescriptor.getMetaSteps(), content.getType() ) ) ) );

        }
        return applicationXDataBuilder.build();
    }

    private XDatas filterXDatasByContentType( final MixinNames mixinNames, final ContentTypeName contentTypeName )
    {
        // add mixins for backwards compatibility (mixins are not filtered by content type)
        final Mixins mixins = this.mixinService.getByNames( mixinNames );
        final Map<XDataName, XData> resultXDatas = new HashMap<>();
        mixins.forEach( mixin -> {
            final XData xData = toXData( mixin );
            resultXDatas.put( xData.getName(), xData );
        } );

        final XDatas xDatas = this.xDataService.getByNames( toXDataNames( mixinNames ) );
        final XDatas filteredXDatas = filterXDatasByContentType( xDatas, contentTypeName );
        filteredXDatas.forEach( ( xData ) -> resultXDatas.put( xData.getName(), xData ) );

        return XDatas.from( resultXDatas.values() );
    }

    private XDatas filterXDatasByContentType( final XDatas xDatas, final ContentTypeName contentTypeName )
    {
        final XDatas.Builder filteredXDatas = XDatas.create();

        final ContentTypeNameWildcardResolver contentTypeNameWildcardResolver =
            new ContentTypeNameWildcardResolver( this.contentTypeService );

        xDatas.forEach( xData -> {
            if ( contentTypeNameWildcardResolver.anyTypeHasWildcard( xData.getAllowContentTypes() ) )
            {
                final ContentTypeNames validContentTypes = ContentTypeNames.from(
                    contentTypeNameWildcardResolver.resolveWildcards( xData.getAllowContentTypes(), xData.getName().getApplicationKey() ) );

                if ( validContentTypes.contains( contentTypeName ) )
                {
                    filteredXDatas.add( xData );
                }
            }
            else if ( xData.getAllowContentTypes().size() > 0 )
            {
                if ( ContentTypeNames.from( xData.getAllowContentTypes() ).contains( contentTypeName ) )
                {
                    filteredXDatas.add( xData );
                }
            }
            else
            {
                filteredXDatas.add( xData );
            }
        } );

        return filteredXDatas.build();
    }

    private XDatas getApplicationXData( final Content content )
    {
        final Site nearestSite = this.contentService.getNearestSite( content.getId() );

        if ( nearestSite != null )
        {
            final List<ApplicationKey> applicationKeys =
                nearestSite.getSiteConfigs().stream().map( SiteConfig::getApplicationKey ).collect( toList() );

            final List<XData> applicationXDatas =
                applicationKeys.stream().flatMap( key -> this.xDataService.getByApplication( key ).stream() ).collect( toList() );

            return XDatas.from( this.filterXDatasByContentType( XDatas.from( applicationXDatas ), content.getType() ).
                stream().iterator() );

        }

        return XDatas.empty();
    }

    private XData toXData( final Mixin mixin )
    {
        XData.Builder xData = XData.create();
        xData.name( XDataName.from( mixin.getName().getApplicationKey(), mixin.getName().getLocalName() ) );
        xData.displayName( mixin.getDisplayName() );
        xData.displayNameI18nKey( mixin.getDisplayNameI18nKey() );
        xData.description( mixin.getDescription() );
        xData.descriptionI18nKey( mixin.getDescriptionI18nKey() );
        xData.createdTime( mixin.getCreatedTime() );
        xData.modifiedTime( mixin.getModifiedTime() );
        xData.creator( mixin.getCreator() );
        xData.modifier( mixin.getModifier() );
        xData.icon( mixin.getIcon() );
        xData.form( mixin.getForm() );
        return xData.build();
    }

    private XDataNames toXDataNames( final MixinNames mixinNames )
    {
        return XDataNames.from( mixinNames.stream().
            map( ( mixinName ) -> XDataName.from( mixinName.getApplicationKey(), mixinName.getLocalName() ) ).
            collect( toList() ) );
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        this.mixinIconResolver = new MixinIconResolver( mixinService );
        this.mixinIconUrlResolver = new MixinIconUrlResolver( this.mixinIconResolver );
    }

    @Reference
    public void setXDataService( final XDataService xDataService )
    {
        this.xDataService = xDataService;
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

