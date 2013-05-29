package com.enonic.wem.portal.dispatch;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class UnderscoreResource
{
    @GET
    @Path("attachment")
    public String handleAttachment()
    {
        return "attachment";
    }

    @GET
    @Path("service")
    public String handleService()
    {
        return "service";
    }
}
