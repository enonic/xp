package com.enonic.xp.portal.impl.jslib.url;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

@Component(immediate = true, service = CommandHandler.class)
public final class AssetUrlHandler
    extends AbstractUrlHandler
{
    public AssetUrlHandler()
    {
        super( "assetUrl" );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final AssetUrlParams params = new AssetUrlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.assetUrl( params );
    }

    @Override
    @Reference
    public void setUrlService( final PortalUrlService value )
    {
        super.setUrlService( value );
    }
}
