package com.enonic.wem.portal.internal.v2;

import javax.ws.rs.GET;

public final class ImageResource
    extends BaseResource
{
    @GET
    public String handle()
    {
        return "Image " + this.contentPath;
    }
}
