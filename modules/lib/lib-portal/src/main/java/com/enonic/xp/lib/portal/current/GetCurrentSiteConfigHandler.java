package com.enonic.xp.lib.portal.current;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

public final class GetCurrentSiteConfigHandler
    implements ScriptBean
{
    private PortalRequest request;

    public PropertyTreeMapper execute()
    {
        final ApplicationKey applicationKey = this.request.getApplicationKey();
        if ( applicationKey != null )
        {
            final SiteConfigs siteConfigs = request.getSite() != null
                ? request.getSite().getSiteConfigs()
                : request.getProject() != null ? request.getProject().getSiteConfigs() : SiteConfigs.empty();

            final SiteConfig config = siteConfigs.get( applicationKey );
            if ( config != null )
            {
                return new PropertyTreeMapper( config.getConfig() );
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
