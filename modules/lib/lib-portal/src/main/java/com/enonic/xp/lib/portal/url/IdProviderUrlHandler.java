package com.enonic.xp.lib.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;

public final class IdProviderUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String idProvider;

    private String redirect;

    private String contextPath;

    private String urlType;

    private Map<String, Collection<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public IdProviderUrlHandler setIdProvider( final String idProvider )
    {
        this.idProvider = idProvider;
        return this;
    }

    public IdProviderUrlHandler setRedirect( final String redirect )
    {
        this.redirect = redirect;
        return this;
    }

    public IdProviderUrlHandler setContextPath( final String contextPath )
    {
        this.contextPath = contextPath;
        return this;
    }

    public IdProviderUrlHandler setUrlType( final String urlType )
    {
        this.urlType = urlType;
        return this;
    }

    public void addQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public String createUrl()
    {
        final IdentityUrlParams params = new IdentityUrlParams().idProviderFunction( null )
            .redirectionUrl( this.redirect )
            .contextPathType( this.contextPath )
            .type( this.urlType )
            .portalRequest( PortalRequestAccessor.get() )
            .idProviderKey( Optional.of( this.idProvider ).map( IdProviderKey::from ).orElse( null ) );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( ( key, values ) -> values.forEach( value -> params.param( key, value ) ) );
        }

        return this.urlServiceSupplier.get().identityUrl( params );
    }
}
