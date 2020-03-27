package com.enonic.xp.project;

import com.google.common.annotations.Beta;

@Beta
public interface ProjectService
{
    Project create( final CreateProjectParams params );

    Project modify( final ModifyProjectParams params );

    Projects list();

    Project get( final ProjectName projectName );

    boolean delete( final ProjectName projectName );

    ProjectPermissions modifyPermissions( final ProjectName projectName, final ProjectPermissions projectPermissions );

    ProjectPermissions getPermissions( final ProjectName projectName );

//    ProjectReadAccess modifyReadAccess( final ProjectName projectName, final ProjectReadAccess readAccess );
//
//    ProjectReadAccess getReadAccess( final ProjectName projectName );
}
