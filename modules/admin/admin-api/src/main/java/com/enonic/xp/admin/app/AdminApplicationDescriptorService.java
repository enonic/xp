package com.enonic.xp.admin.app;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.PrincipalKeys;

@Beta
public interface AdminApplicationDescriptorService
{
    AdminApplicationDescriptors getAll();

    AdminApplicationDescriptors getAllowedApplications( PrincipalKeys principalKeys );
}
