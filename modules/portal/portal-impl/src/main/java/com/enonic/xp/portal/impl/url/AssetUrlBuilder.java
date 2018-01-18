package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.url.AssetUrlParams;

final class AssetUrlBuilder
    extends GenericEndpointUrlBuilder<AssetUrlParams>
{
    public AssetUrlBuilder()
    {
        super( "asset" );
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        Application application = resolveApplication();
        String applicationKey = application.getKey().toString();
        String modifiedTime = Long.toString( application.getModifiedTime().getEpochSecond() );
        appendPart( url, applicationKey + ":" + modifiedTime );

        appendPart( url, this.params.getPath() );
    }

    private Application resolveApplication()
    {
        final ApplicationKey applicationKey = new ApplicationResolver().
            portalRequest( this.portalRequest ).
            application( this.params.getApplication() ).
            resolve();

        final Application application = this.applicationService.getInstalledApplication( applicationKey );
        if ( application == null )
        {
            throw new IllegalArgumentException( "Could not find application [" + applicationKey + "]" );
        }
        return application;
    }
}
