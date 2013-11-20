package com.enonic.wem.admin.rest.resource.module.json;


import com.enonic.wem.admin.json.module.ModuleSummaryJson;
import com.enonic.wem.admin.rest.ResultJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;

public class InstallModuleResultJson extends ResultJson<ModuleSummaryJson>
{

    private InstallModuleResultJson( final ModuleSummaryJson moduleJson, final ErrorJson error )
    {
        super(moduleJson, error);
    }

    public static InstallModuleResultJson error( final String message )
    {
        return new InstallModuleResultJson( null, new ErrorJson( message ) );
    }

    public static InstallModuleResultJson result( final ModuleSummaryJson moduleJson )
    {
        return new InstallModuleResultJson( moduleJson, null );
    }
}
