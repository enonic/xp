package com.enonic.wem.portal.internal.v2;

import javax.ws.rs.GET;

public abstract class RenderResource
    extends BaseResource
{
    @GET
    public final String doGet()
    {
        return "Render " + this.contentPath.toString();
    }
}
