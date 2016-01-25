package com.enonic.xp.admin.tool;

import com.google.common.annotations.Beta;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

@Beta
public interface AdminToolDescriptorService
{
    AdminToolDescriptors getAllowedAdminToolDescriptors( final PrincipalKeys principalKeys );

    AdminToolDescriptor getByKey( final DescriptorKey descriptorKey );
}
