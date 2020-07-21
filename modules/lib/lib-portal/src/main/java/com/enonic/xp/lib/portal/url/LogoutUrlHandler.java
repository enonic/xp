package com.enonic.xp.lib.portal.url;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.xp.context.Context;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class LogoutUrlHandler
    extends AbstractUrlHandler
{
    private static final Set<String> VALID_URL_PROPERTY_KEYS =
        new HashSet<>( Arrays.asList( "redirect", "contextPath", "type", "params" ) );

    private Context context;

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( request ).
            idProviderFunction( "logout" ).
            idProviderKey( retrieveIdProviderKey() ).
            setAsMap( map );

        return this.urlService.identityUrl( params );
    }

    private IdProviderKey retrieveIdProviderKey()
    {
        final AuthenticationInfo authInfo = this.context.getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return authInfo.getUser().
                getKey().
                getIdProviderKey();
        }
        return null;
    }

    @Override
    protected boolean isValidParam( final String param )
    {
        return VALID_URL_PROPERTY_KEYS.contains( param );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        super.initialize( context );
        this.context = context.getBinding( Context.class ).get();
    }
}
