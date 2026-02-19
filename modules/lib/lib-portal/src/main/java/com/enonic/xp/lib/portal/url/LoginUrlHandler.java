package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;

public final class LoginUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String idProvider;

    private String redirect;

    private String contextPath;

    private String urlType;

    private Map<String, List<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public LoginUrlHandler setIdProvider( final String idProvider )
    {
        this.idProvider = idProvider;
        return this;
    }

    public LoginUrlHandler setRedirect( final String redirect )
    {
        this.redirect = redirect;
        return this;
    }

    public LoginUrlHandler setContextPath( final String contextPath )
    {
        this.contextPath = contextPath;
        return this;
    }

    public LoginUrlHandler setUrlType( final String urlType )
    {
        this.urlType = urlType;
        return this;
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public String createUrl()
    {
        final IdentityUrlParams params = new IdentityUrlParams().idProviderFunction( "login" )
            .redirectionUrl( this.redirect )
            .contextPathType( this.contextPath )
            .type( this.urlType )
            .idProviderKey( Optional.ofNullable( this.idProvider ).map( IdProviderKey::from ).orElse( null ) );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( params::param );
        }

        return this.urlServiceSupplier.get().identityUrl( params );
    }
}
