package com.enonic.wem.web.rest.rpc.system;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.Version;
import com.enonic.wem.web.json.JsonResult;

final class SystemInfoJsonResult
    extends JsonResult
{
    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "installationName", "production" );
        json.put( "version", Version.get().getVersion() );
        json.put( "title", Version.get().getName() );
    }
}
