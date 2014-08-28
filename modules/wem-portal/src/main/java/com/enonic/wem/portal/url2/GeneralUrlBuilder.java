package com.enonic.wem.portal.url2;

import java.util.Map;

import static com.google.common.base.Strings.emptyToNull;

public final class GeneralUrlBuilder
    extends PortalUrlBuilder<GeneralUrlBuilder>
{
    private String optionPath;

    public GeneralUrlBuilder optionPath( final String optionPath )
    {
        this.optionPath = emptyToNull( optionPath );
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Map<String, String> params )
    {
        super.buildUrl( url, params );

        if ( this.optionPath != null )
        {
            appendPart( url, "_" );
            appendPart( url, this.optionPath );
        }
    }
}
