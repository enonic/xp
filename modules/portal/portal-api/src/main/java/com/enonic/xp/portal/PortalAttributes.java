package com.enonic.xp.portal;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PortalAttributes
{
    private String baseUri;

    private RenderMode renderMode;

    public String getBaseUri()
    {
        return baseUri;
    }

    public void setBaseUri( final String baseUri )
    {
        this.baseUri = baseUri;
    }

    public RenderMode getRenderMode()
    {
        return renderMode;
    }

    public void setRenderMode( final RenderMode renderMode )
    {
        this.renderMode = renderMode;
    }
}
