package com.enonic.wem.portal.internal.v2;

import javax.ws.rs.Path;

public final class UnderscoreResource
    extends BaseResource
{
    @Path("image")
    public ImageResource image()
    {
        return newResource( ImageResource.class );
    }
}
