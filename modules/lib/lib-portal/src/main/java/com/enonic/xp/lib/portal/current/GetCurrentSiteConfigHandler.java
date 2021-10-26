package com.enonic.xp.lib.portal.current;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.Site;

public final class GetCurrentSiteConfigHandler
    implements ScriptBean
{
    private PortalRequest request;

    public PropertyTreeMapper execute()
    {
        final ApplicationKey applicationKey = this.request.getApplicationKey();
        if ( applicationKey != null )
        {
            final Site site = this.request.getSite();
            if ( site != null )
            {
                final PropertyTree siteConfigPropertyTree = site.getSiteConfig( applicationKey );
                if ( siteConfigPropertyTree != null )
                {
                    return new PropertyTreeMapper( siteConfigPropertyTree );
                }
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
