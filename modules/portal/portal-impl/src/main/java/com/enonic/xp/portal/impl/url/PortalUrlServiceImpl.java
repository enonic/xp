package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlGeneratorParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.style.StyleDescriptorService;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

@Component(immediate = true, configurationPid = "com.enonic.xp.portal")
public final class PortalUrlServiceImpl
    implements PortalUrlService
{
    private final ContentService contentService;

    private final ResourceService resourceService;

    private final MacroService macroService;

    private final StyleDescriptorService styleDescriptorService;

    private final RedirectChecksumService redirectChecksumService;

    private final UrlGeneratorParamsAdapter urlStrategyFacade;

    private volatile boolean legacyImageServiceEnabled;

    private volatile boolean legacyAttachmentServiceEnabled;

    private volatile boolean useLegacyAssetContextPath;

    private volatile boolean useLegacyIdProviderContextPath;

    @Activate
    public PortalUrlServiceImpl( @Reference final ContentService contentService, @Reference final ResourceService resourceService,
                                 @Reference final MacroService macroService, @Reference final StyleDescriptorService styleDescriptorService,
                                 @Reference final RedirectChecksumService redirectChecksumService,
                                 @Reference final UrlGeneratorParamsAdapter urlStrategyFacade )
    {
        this.contentService = contentService;
        this.resourceService = resourceService;
        this.macroService = macroService;
        this.styleDescriptorService = styleDescriptorService;
        this.redirectChecksumService = redirectChecksumService;
        this.urlStrategyFacade = urlStrategyFacade;
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        this.legacyImageServiceEnabled = config.legacy_imageService_enabled();
        this.legacyAttachmentServiceEnabled = config.legacy_attachmentService_enabled();
        this.useLegacyAssetContextPath = config.asset_legacyContextPath();
        this.useLegacyIdProviderContextPath = config.idprovider_legacyContextPath();
    }

    @Override
    public String assetUrl( final AssetUrlParams params )
    {
        final AssetUrlBuilder builder = new AssetUrlBuilder();
        builder.setUseLegacyContextPath( useLegacyAssetContextPath );
        return build( builder, params );
    }

    @Override
    public String serviceUrl( final ServiceUrlParams params )
    {
        return build( new ServiceUrlBuilder(), params );
    }

    @Override
    public String pageUrl( final PageUrlParams params )
    {
        final PageUrlGeneratorParams generatorParams = params.isOffline() || PortalRequestAccessor.get() == null
            ? urlStrategyFacade.offlinePageUrlParams( params )
            : urlStrategyFacade.requestPageUrlParams( params );

        return pageUrl( generatorParams );
    }

    @Override
    public String componentUrl( final ComponentUrlParams params )
    {
        return build( new ComponentUrlBuilder(), params );
    }

    @Override
    public String imageUrl( final ImageUrlParams params )
    {
        if ( this.legacyImageServiceEnabled )
        {
            return build( new ImageUrlBuilder(), params );
        }
        else
        {
            final ImageUrlGeneratorParams generatorParams = params.isOffline() || PortalRequestAccessor.get() == null
                ? urlStrategyFacade.offlineImageUrlParams( params )
                : urlStrategyFacade.requestImageUrlParams( params );

            return imageUrl( generatorParams );
        }
    }

    @Override
    public String attachmentUrl( final AttachmentUrlParams params )
    {
        if ( this.legacyAttachmentServiceEnabled )
        {
            return build( new AttachmentUrlBuilder(), params );
        }
        else
        {
            final AttachmentUrlGeneratorParams generatorParams = params.isOffline() || PortalRequestAccessor.get() == null
                ? urlStrategyFacade.offlineAttachmentUrlParams( params )
                : urlStrategyFacade.requestAttachmentUrlParams( params );

            return attachmentUrl( generatorParams );
        }
    }

    @Override
    public String identityUrl( final IdentityUrlParams params )
    {
        final IdentityUrlBuilder builder = new IdentityUrlBuilder( redirectChecksumService::generateChecksum );
        builder.setUseLegacyContextPath( useLegacyIdProviderContextPath );

        return build( builder, params );
    }

    @Override
    public String generateUrl( final GenerateUrlParams params )
    {
        return build( new GenerateUrlBuilder(), params );
    }

    @Override
    public String processHtml( final ProcessHtmlParams params )
    {
        return new RichTextProcessor( styleDescriptorService, this, macroService ).process( params );
    }

    @Override
    public String apiUrl( final ApiUrlParams params )
    {
        Objects.requireNonNull( params, "\"params\" is required" );
        Objects.requireNonNull( params.getApi(), "\"api\" is required" );
        if ( params.getPortalRequest() != null )
        {
            return build( new UniversalApiUrlBuilder(), params );
        }
        else
        {
            Objects.requireNonNull( params.getApplication(), "\"application\" is required" );
            // TODO resolve baseUrl
            return new SlashApiUrlBuilder( params ).build();
        }
    }

    @Override
    public String imageUrl( final ImageUrlGeneratorParams params )
    {
        final ImageMediaPathStrategyParams imageMediaPathStrategyParams = ImageMediaPathStrategyParams.create()
            .setMedia( params.getMedia() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setScale( params.getScale() )
            .setFormat( params.getFormat() )
            .build();

        final ApiUrlGeneratorParams apiUrlParams = ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( params.getBaseUrlStrategy() )
            .setApplication( "media" )
            .setApi( "image" )
            .setPath( () -> new ImageMediaPathStrategy( imageMediaPathStrategyParams ).generatePath() )
            .putNotNullQueryParam( "quality", Objects.toString( params.getQuality(), null ) )
            .putNotNullQueryParam( "background", params.getBackground() )
            .putNotNullQueryParam( "filter", params.getFilter() )
            .putQueryParams( params.getQueryParams() )
            .build();

        return apiUrl( apiUrlParams );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlGeneratorParams params )
    {
        final AttachmentMediaPathStrategyParams strategyParams = AttachmentMediaPathStrategyParams.create()
            .setMedia( params.getMediaSupplier() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setDownload( params.isDownload() )
            .build();

        final DefaultQueryParamsStrategy queryParamsStrategy = new DefaultQueryParamsStrategy();
        if ( strategyParams.isDownload() )
        {
            queryParamsStrategy.put( "download", null );
        }
        params.getQueryParams().forEach( queryParamsStrategy::putAll );

        return runWithAdminRole(
            () -> UrlGenerator.generateUrl( params.getBaseUrlStrategy(), new AttachmentMediaPathStrategy( strategyParams ),
                                            queryParamsStrategy ) );
    }

    @Override
    public String pageUrl( final PageUrlGeneratorParams params )
    {
        final DefaultQueryParamsStrategy queryParamsStrategy = new DefaultQueryParamsStrategy();
        params.getQueryParams().forEach( queryParamsStrategy::putAll );

        return runWithAdminRole( () -> UrlGenerator.generateUrl( params.getBaseUrlStrategy(), () -> "", queryParamsStrategy ) );
    }

    @Override
    public String apiUrl( final ApiUrlGeneratorParams params )
    {
        final PathStrategy pathStrategy = () -> {
            final StringBuilder url = new StringBuilder();
            appendPart( url, params.getApplication() + ":" + params.getApi() );
            appendPart( url, params.getPath().get() );
            return url.toString();
        };

        final DefaultQueryParamsStrategy queryParamsStrategy = new DefaultQueryParamsStrategy();
        params.getQueryParams().forEach( queryParamsStrategy::putAll );

        return runWithAdminRole( () -> UrlGenerator.generateUrl( params.getBaseUrlStrategy(), pathStrategy, queryParamsStrategy ) );
    }

    private <B extends PortalUrlBuilder<P>, P extends AbstractUrlParams> String build( final B builder, final P params )
    {
        builder.setParams( params );
        builder.contentService = this.contentService;
        builder.resourceService = this.resourceService;
        return runWithAdminRole( builder::build );
    }

    private <T> T runWithAdminRole( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return ContextBuilder.from( context ).authInfo( authenticationInfo ).build().callWith( callable );
    }
}
