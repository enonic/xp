package com.enonic.wem.admin.rest.resource.module.json;


import com.enonic.wem.admin.json.module.ModuleSummaryJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;

public class InstallModuleResultJson
{
    private final ModuleSummaryJson result;

    private final ErrorJson error;

    private InstallModuleResultJson( final ModuleSummaryJson moduleJson, final ErrorJson error )
    {
        this.error = error;
        this.result = moduleJson;
    }

    public static InstallModuleResultJson error( final String message )
    {
        return new InstallModuleResultJson( null, new ErrorJson( message ) );
    }

    public static InstallModuleResultJson result( final ModuleSummaryJson moduleJson )
    {
        return new InstallModuleResultJson( moduleJson, null );
    }

    public ModuleSummaryJson getResult()
    {
        return result;
    }

    public ErrorJson getError()
    {
        return error;
    }
}
