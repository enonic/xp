package com.enonic.xp.portal.impl.url;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.ApplicationUrlParams;
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
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public final class PortalUrlServiceImpl
    implements PortalUrlService
{
    private ContentService contentService;

    private ApplicationService applicationService;

    private MacroService macroService;

    @Override
    public String assetUrl( final AssetUrlParams params )
    {
        return build( new AssetUrlBuilder(), params );
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
        return build( new ImageUrlBuilder(), params );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlParams params )
    {
        return build( new AttachmentUrlBuilder(), params );
    }

    @Override
    public String identityUrl( final IdentityUrlParams params )
    {
        return build( new IdentityUrlBuilder(), params );
    }

    @Override
    public String applicationUrl( final ApplicationUrlParams params )
    {
        return build( new ApplicationUrlBuilder(), params );
    }

    @Override
    public String generateUrl( final GenerateUrlParams params )
    {
        return build( new GenerateUrlBuilder(), params );
    }

    @Override
    public String processHtml( final ProcessHtmlParams params )
    {
        if ( params.getValue() == null || params.getValue().isEmpty() )
        {
            return "";
        }

        String processedHtml = new HtmlLinkProcessor( contentService, this ).
            process( params.getValue(), params.getType(), params.getPortalRequest() );
        processedHtml = new HtmlMacroProcessor( macroService ).
            process( processedHtml );
        return processedHtml;
    }

    private <B extends PortalUrlBuilder<P>, P extends AbstractUrlParams> String build( final B builder, final P params )
    {
        builder.setParams( params );
        builder.contentService = this.contentService;
        builder.applicationService = this.applicationService;
        return runWithAdminRole( () -> builder.build() );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }


    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setMacroService( final MacroService macroService )
    {
        this.macroService = macroService;
    }

    private <T> T runWithAdminRole( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.copyOf( context.getAuthInfo() ).
            principals( RoleKeys.ADMIN ).
            build();
        return ContextBuilder.from( context ).
            authInfo( authenticationInfo ).
            build().
            callWith( callable );
    }
}
