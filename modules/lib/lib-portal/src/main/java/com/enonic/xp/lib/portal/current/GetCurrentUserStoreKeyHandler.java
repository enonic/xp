package com.enonic.xp.lib.portal.current;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public final class GetCurrentUserStoreKeyHandler
    implements ScriptBean
{
    private PortalRequest request;

    public String execute()
    {
        final UserStoreKey userStoreKey = retrieveUserStoreKey( request.getRawRequest() );
        if ( userStoreKey != null )
        {
            return userStoreKey.toString();
        }
        return null;
    }

    private UserStoreKey retrieveUserStoreKey( final HttpServletRequest req )
    {
        final UserStore userStore = this.request.getUserStore();
        if ( userStore != null )
        {
            return userStore.getKey();
        }
        
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        if ( virtualHost != null )
        {
            return virtualHost.getUserStoreKey();
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
    }
}
