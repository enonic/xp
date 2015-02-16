package com.enonic.xp.portal.impl.jslib.url;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

@Component(immediate = true, service = CommandHandler.class)
public final class ImageUrlHandler
    extends AbstractUrlHandler
{
    public ImageUrlHandler()
    {
        super( "imageUrl" );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ImageUrlParams params = new ImageUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.imageUrl( params );
    }

    @Override
    @Reference
    public void setUrlService( final PortalUrlService value )
    {
        super.setUrlService( value );
    }
}
