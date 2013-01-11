package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

public class GenerateContentNameJsonResult
    extends JsonResult
{

    private String contentName;

    public GenerateContentNameJsonResult( String contentName )
    {
        this.contentName = contentName;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "contentName", contentName );
    }
}
