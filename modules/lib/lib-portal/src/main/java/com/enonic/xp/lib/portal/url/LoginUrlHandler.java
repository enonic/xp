package com.enonic.xp.lib.portal.url;

import java.util.Optional;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;

public final class LoginUrlHandler
    extends AbstractUrlHandler
{
    private SecurityService securityService;

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {

        final Optional<PathGuard> pathGuardOptional = securityService.getPathGuardByPath( request.getBaseUri() );
        final UserStoreKey userStoreKey = pathGuardOptional.isPresent() ? pathGuardOptional.get().getUserStoreKey() : null;

        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( request ).
            idProviderFunction( "login" ).
            userStoreKey( userStoreKey == null ? UserStoreKey.system() : userStoreKey ).
            setAsMap( map );
        return this.urlService.identityUrl( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        super.initialize( context );
        this.securityService = context.getService( SecurityService.class ).get();
    }
}
