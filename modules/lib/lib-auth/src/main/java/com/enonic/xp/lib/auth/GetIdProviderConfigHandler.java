package com.enonic.xp.lib.auth;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.content.mapper.PropertyTreeMapper;
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
        final AuthConfig authConfig = this.request.getUserStore() == null ? null : this.request.getUserStore().getAuthConfig();
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

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
    }
}
