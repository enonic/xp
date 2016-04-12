package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.context.Context;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class LogoutUrlHandler
    extends AbstractUrlHandler
{

    private Context context;

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final AuthenticationInfo authInfo = this.context.getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            UserStoreKey userStoreKey = authInfo.getUser().
                getKey().
                getUserStore();
            final IdentityUrlParams params = new IdentityUrlParams().
                portalRequest( request ).
                idProviderFunction( "logout" ).
                userStoreKey( userStoreKey ).
                setAsMap( map );

            return this.urlService.identityUrl( params );
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        super.initialize( context );
        this.context = context.getBinding( Context.class ).get();
    }
}
