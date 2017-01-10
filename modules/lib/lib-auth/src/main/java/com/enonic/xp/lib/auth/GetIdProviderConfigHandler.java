package com.enonic.xp.lib.auth;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.UserStore;

public final class GetIdProviderConfigHandler
    implements ScriptBean
{
    private PortalRequest request;

    public PropertyTreeMapper execute()
    {
        final AuthConfig authConfig = retrieveAuthConfig();
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

    private AuthConfig retrieveAuthConfig()
    {
        final UserStore userStore = this.request.getUserStore();
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
    }
}
