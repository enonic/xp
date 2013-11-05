package com.enonic.wem.admin.rest.resource.schema.json;

public class ErrorJson
{

    private String msg;

    public ErrorJson( final String msg )
    {
        this.msg = msg;
    }

    public String getMsg()
    {
        return msg;
    }

}
