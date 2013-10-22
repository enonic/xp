package com.enonic.wem.admin.rpc.content;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

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
