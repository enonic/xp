package com.enonic.xp.core.impl.project;

import com.enonic.xp.context.Context;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.auth.AuthenticationInfo;

public interface ProjectPermissionsContextManager
{
    Context initCreateContext();

    Context initDeleteContext();

    Context initListContext();

    Context initGetContext( ProjectName projectName );

    Context initUpdateContext( ProjectName projectName );

    boolean hasAdminAccess( final AuthenticationInfo authenticationInfo );

    boolean hasManagerAccess( final AuthenticationInfo authenticationInfo );

    boolean hasAnyProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo );


}
