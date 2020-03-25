package com.enonic.xp.core.impl.project;

import com.enonic.xp.context.Context;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.auth.AuthenticationInfo;

public interface ProjectPermissionsContextManager
{
    Context initCreateContext();

    Context initDeleteContext( final ProjectName projectName );

    Context initListContext();

    Context initGetContext( final ProjectName projectName );

    Context initUpdateContext( final ProjectName projectName );

    boolean hasAdminAccess( final AuthenticationInfo authenticationInfo );

    boolean hasManagerAccess( final AuthenticationInfo authenticationInfo );

    boolean hasAnyProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo );


}
