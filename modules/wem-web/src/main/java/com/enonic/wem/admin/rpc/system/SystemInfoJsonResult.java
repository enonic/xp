package com.enonic.wem.admin.rpc.system;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.Version;

final class SystemInfoJsonResult
    extends JsonResult
{
    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "installationName", "production" ); // NOTE! If installationName is not set, this string should be blank (CMS-845)
        json.put( "version", Version.get().getVersion() );
        json.put( "title", Version.get().getName() );
    }
}
