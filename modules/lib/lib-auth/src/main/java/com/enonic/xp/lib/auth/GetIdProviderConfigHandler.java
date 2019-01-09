package com.enonic.xp.lib.auth;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;

public final class GetIdProviderConfigHandler
    implements ScriptBean
{
    private PortalRequest request;

    public PropertyTreeMapper execute()
    {
        final IdProviderConfig idProviderConfig = retrieveIdProviderConfig();
        if ( idProviderConfig != null )
        {
            final PropertyTree configPropertyTree = idProviderConfig.getConfig();
            if ( configPropertyTree != null )
            {
                return new PropertyTreeMapper( configPropertyTree );
            }
        }

        return null;
    }

    private IdProviderConfig retrieveIdProviderConfig()
    {
        final IdProvider idProvider = this.request.getIdProvider();
        if ( idProvider != null )
        {
            return idProvider.getIdProviderConfig();
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
    }
}
