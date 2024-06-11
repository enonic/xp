package com.enonic.xp.portal.impl.url;

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
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.style.StyleDescriptorService;

@Component(immediate = true, configurationPid = "com.enonic.xp.portal")
public final class PortalUrlServiceImpl
    implements PortalUrlService
{
    private final ContentService contentService;

    private final ResourceService resourceService;

    private final MacroService macroService;

    private final StyleDescriptorService styleDescriptorService;

    private final RedirectChecksumService redirectChecksumService;

    private volatile boolean legacyImageServiceEnabled;

    private volatile boolean legacyAttachmentServiceEnabled;

    private volatile boolean useLegacyAssetContextPath;

    private volatile boolean useLegacyIdProviderContextPath;

    @Activate
    public PortalUrlServiceImpl( @Reference final ContentService contentService, @Reference final ResourceService resourceService,
                                 @Reference final MacroService macroService, @Reference final StyleDescriptorService styleDescriptorService,
                                 @Reference final RedirectChecksumService redirectChecksumService )
    {
        this.contentService = contentService;
        this.resourceService = resourceService;
        this.macroService = macroService;
        this.styleDescriptorService = styleDescriptorService;
        this.redirectChecksumService = redirectChecksumService;
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
        return build( new PageUrlBuilder(), params );
    }

    @Override
    public String componentUrl( final ComponentUrlParams params )
    {
        return build( new ComponentUrlBuilder(), params );
    }

    @Override
    public String imageUrl( final ImageUrlParams params )
    {
        final ImageUrlBuilder builder = new ImageUrlBuilder();
        builder.setLegacyImageServiceEnabled( this.legacyImageServiceEnabled );

        return build( builder, params );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlParams params )
    {
        final AttachmentUrlBuilder builder = new AttachmentUrlBuilder();
        builder.setLegacyAttachmentServiceEnabled( this.legacyAttachmentServiceEnabled );

        return build( builder, params );
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
