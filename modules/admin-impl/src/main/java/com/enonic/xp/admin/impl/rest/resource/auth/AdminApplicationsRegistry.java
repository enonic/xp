package com.enonic.xp.admin.impl.rest.resource.auth;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.core.security.PrincipalKeys;
import com.enonic.xp.core.security.RoleKeys;

import static java.util.stream.Collectors.toList;

final class AdminApplicationsRegistry
{
    private static final AdminApplication CONTENT_MANAGER_APP = new AdminApplication( "content-manager", PrincipalKeys.from( RoleKeys.ADMIN,
                                                                                                                             RoleKeys.CONTENT_MANAGER_ADMIN,
                                                                                                                             RoleKeys.CONTENT_MANAGER_APP ) );

    private static final AdminApplication USER_MANAGER_APP = new AdminApplication( "user-manager", PrincipalKeys.from( RoleKeys.ADMIN,
                                                                                                                       RoleKeys.USER_MANAGER_ADMIN,
                                                                                                                       RoleKeys.USER_MANAGER_APP ) );

    private static final AdminApplication MODULE_MANAGER_APP =
        new AdminApplication( "module-manager", PrincipalKeys.from( RoleKeys.ADMIN ) );


    private final List<AdminApplication> applications;

    public AdminApplicationsRegistry()
    {
        this.applications = new ArrayList<>();
        add( CONTENT_MANAGER_APP );
        add( USER_MANAGER_APP );
        add( MODULE_MANAGER_APP );
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
