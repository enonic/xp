package com.enonic.xp.admin.adminapp;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.PrincipalKeys;

@Beta
public interface AdminApplicationDescriptorService
{
    AdminApplicationDescriptors getAllowedAdminApplicationDescriptors( final PrincipalKeys principalKeys );
}
