package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.content.mapper.PropertyTreeMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public final class GetIdProviderConfigHandler
    implements ScriptBean
{
    private PortalRequest request;

    private Supplier<SecurityService> securityService;

    public PropertyTreeMapper execute()
    {
        final UserStoreKey userStoreKey = retrieveUserStoreKey( request.getRawRequest() );
        final UserStore userStore = retrieveUserStore( userStoreKey );
        final AuthConfig authConfig = retrieveAuthConfig( userStore );
        if ( authConfig != null )
        {
            final PropertyTree configPropertyTree = authConfig.getConfig();
            if ( configPropertyTree != null )
            {
                return new PropertyTreeMapper( configPropertyTree );
            }
        }

        return null;
    }

    private UserStoreKey retrieveUserStoreKey( final HttpServletRequest req )
    {
        if ( this.request.getUserStoreKey() != null )
        {
            return this.request.getUserStoreKey();
        }
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        if ( virtualHost != null )
        {
            return virtualHost.getUserStoreKey();
        }
        return null;
    }


    private UserStore retrieveUserStore( final UserStoreKey userStoreKey )
    {
        if ( userStoreKey != null )
        {
            return securityService.get().getUserStore( userStoreKey );
        }
        return null;
    }

    private AuthConfig retrieveAuthConfig( final UserStore userStore )
    {
        if ( userStore != null )
        {
            return userStore.getAuthConfig();
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
        this.securityService = context.getService( SecurityService.class );
    }
}
