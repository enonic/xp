package com.enonic.xp.project;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ProjectService
{
    Project create( final CreateProjectParams params );

    Project modify( final ModifyProjectParams params );

    void modifyIcon( final ModifyProjectIconParams params );

    ByteSource getIcon( final ProjectName projectName );

    Projects list();

    ProjectGraph graph( final ProjectName projectName );

    Project get( final ProjectName projectName );

    boolean delete( final ProjectName projectName );

    ProjectPermissions modifyPermissions( final ProjectName projectName, final ProjectPermissions projectPermissions );

    ProjectPermissions getPermissions( final ProjectName projectName );
}
