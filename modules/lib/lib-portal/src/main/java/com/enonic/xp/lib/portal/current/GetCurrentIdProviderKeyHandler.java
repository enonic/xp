package com.enonic.xp.lib.portal.current;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public final class GetCurrentIdProviderKeyHandler
    implements ScriptBean
{
    private PortalRequest request;

    public String execute()
    {
        final IdProviderKey idProviderKey = retrieveIdProviderKey( request.getRawRequest() );
        if ( idProviderKey != null )
        {
            return idProviderKey.toString();
        }
        return null;
    }

    private IdProviderKey retrieveIdProviderKey( final HttpServletRequest req )
    {
        final IdProvider idProvider = this.request.getIdProvider();
        if ( idProvider != null )
        {
            return idProvider.getKey();
        }
        
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        if ( virtualHost != null )
        {
            return virtualHost.getIdProviderKey();
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
    }
}
