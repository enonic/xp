package com.enonic.xp.project;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKeys;

@PublicApi
public interface ProjectService
{
    Project create( CreateProjectParams params );

    Project modify( ModifyProjectParams params );

    void modifyIcon( ModifyProjectIconParams params );

    ByteSource getIcon( ProjectName projectName );

    ApplicationKeys getAvailableApplications( ProjectName projectName );

    Projects list();

    ProjectGraph graph( ProjectName projectName );

    Project get( ProjectName projectName );

    boolean delete( ProjectName projectName );

    ProjectPermissions modifyPermissions( ProjectName projectName, ProjectPermissions projectPermissions );

    ProjectPermissions getPermissions( ProjectName projectName );
}
