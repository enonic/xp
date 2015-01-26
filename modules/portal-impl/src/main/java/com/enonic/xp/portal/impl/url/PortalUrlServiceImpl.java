package com.enonic.xp.portal.impl.url;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;

@Component(immediate = true)
public final class PortalUrlServiceImpl
    implements PortalUrlService
{
    @Override
    public String assetUrl( final AssetUrlParams params )
    {
        return build( new AssetUrlBuilder(), params );
    }

    @Override
    public String serviceUrl( final ServiceUrlParams params )
    {
        return build( new ServiceUrlBuilder(), params );
    }

    private <B extends PortalUrlBuilder<P>, P extends AbstractUrlParams> String build( final B builder, final P params )
    {
        builder.setParams( params );
        return builder.build();
    }
}
