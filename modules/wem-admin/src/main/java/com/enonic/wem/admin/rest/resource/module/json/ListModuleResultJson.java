package com.enonic.wem.admin.rest.resource.module.json;

import com.enonic.wem.admin.json.module.ListModuleJson;
import com.enonic.wem.admin.rest.ResultJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;

public class ListModuleResultJson extends ResultJson<ListModuleJson> {

    private ListModuleResultJson(ListModuleJson result, ErrorJson error) {
        super(result, error);
    }

   public static ListModuleResultJson error( final String message )
    {
        return new ListModuleResultJson( null, new ErrorJson( message ) );
    }

    public static ListModuleResultJson result( final ListModuleJson moduleJson )
    {
        return new ListModuleResultJson( moduleJson, null );
    }
}
