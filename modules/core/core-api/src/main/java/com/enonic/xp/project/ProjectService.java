package com.enonic.xp.project;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.security.acl.AccessControlList;


@NullMarked
public interface ProjectService
{
    Project create( CreateProjectParams params );

    Project modify( ModifyProjectParams params );

    void modifyIcon( ModifyProjectIconParams params );

    @Nullable ByteSource getIcon( ProjectName projectName );

    ApplicationKeys getAvailableApplications( ProjectName projectName );

    Projects list();

    ProjectGraph graph( ProjectName projectName );

    @Nullable Project get( ProjectName projectName );

    boolean delete( ProjectName projectName );

    ProjectPermissions modifyPermissions( ProjectName projectName, ProjectPermissions projectPermissions );

    boolean setReadAccess( SetProjectReadAccessParams params );

    ProjectPermissions getPermissions( ProjectName projectName );

    AccessControlList getRootPermissions( ProjectName projectName );

    boolean getReadAccess( ProjectName projectName );
}
