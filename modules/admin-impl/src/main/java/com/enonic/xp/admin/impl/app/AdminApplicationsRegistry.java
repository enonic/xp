package com.enonic.xp.admin.impl.app;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

import static java.util.stream.Collectors.toList;

public final class AdminApplicationsRegistry
{
    private static final AdminApplication CONTENT_MANAGER_APP = new AdminApplication( "content-manager", PrincipalKeys.from( RoleKeys.ADMIN,
                                                                                                                             RoleKeys.CONTENT_MANAGER_ADMIN,
                                                                                                                             RoleKeys.CONTENT_MANAGER_APP ) );

    private static final AdminApplication USER_MANAGER_APP = new AdminApplication( "user-manager", PrincipalKeys.from( RoleKeys.ADMIN,
                                                                                                                       RoleKeys.USER_MANAGER_ADMIN,
                                                                                                                       RoleKeys.USER_MANAGER_APP ) );

    private static final AdminApplication APPLICATIONS_APP = new AdminApplication( "applications", PrincipalKeys.from( RoleKeys.ADMIN ) );


    private final List<AdminApplication> applications;

    public AdminApplicationsRegistry()
    {
        this.applications = new ArrayList<>();
        add( CONTENT_MANAGER_APP );
        add( USER_MANAGER_APP );
        add( APPLICATIONS_APP );
    }

    public AdminApplicationsRegistry add( final AdminApplication application )
    {
        this.applications.add( application );
        return this;
    }

    public List<AdminApplication> getAllowedApplications( final PrincipalKeys authPrincipals )
    {
        return applications.stream().
            filter( ( app ) -> app.isAccessAllowed( authPrincipals ) ).
            collect( toList() );
    }
}
