package com.enonic.xp.lib.portal.current;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.content.mapper.PropertyTreeMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.Site;

public final class GetCurrentSiteConfigHandler
    implements ScriptBean
{
    private PortalRequest request;

    public PropertyTreeMapper execute()
    {
        final Site site = this.request.getSite();
        final ApplicationKey applicationKey = this.request.getApplicationKey();
        if ( site != null && applicationKey != null )
        {
            final PropertyTree siteConfigPropertyTree = site.getSiteConfig( applicationKey );
            if ( siteConfigPropertyTree != null )
            {
                return new PropertyTreeMapper( siteConfigPropertyTree );
            }
        }

        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = PortalRequestAccessor.get();
    }
}
