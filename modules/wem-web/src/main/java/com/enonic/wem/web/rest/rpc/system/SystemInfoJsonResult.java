package com.enonic.wem.web.rest.rpc.system;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

import com.enonic.cms.api.Version;

final class SystemInfoJsonResult
    extends JsonResult
{
    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "installationName", "prod" );
        json.put( "version", Version.getVersion() );
        json.put( "title", Version.getTitle() );
    }
}
