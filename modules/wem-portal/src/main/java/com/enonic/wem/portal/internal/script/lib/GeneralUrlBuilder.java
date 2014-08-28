package com.enonic.wem.portal.internal.script.lib;


public final class GeneralUrlBuilder
    extends PortalUrlBuilder<GeneralUrlBuilder>
{
    private GeneralUrlBuilder( final String baseUrl )
    {
        super( baseUrl );
    }

    public static GeneralUrlBuilder createUrl( final String baseUrl )
    {
        return new GeneralUrlBuilder( baseUrl );
    }
}
