package com.enonic.wem.portal.internal.script.lib;


public final class PortalUrlBuilder
    extends BasePortalUrlBuilder<PortalUrlBuilder>
{
    private PortalUrlBuilder( final String baseUrl )
    {
        super( baseUrl );
    }

    public static PortalUrlBuilder createUrl( final String baseUrl )
    {
        return new PortalUrlBuilder( baseUrl );
    }
}
