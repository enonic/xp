package com.enonic.xp.admin.tool;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

@Beta
public interface AdminToolDescriptorService
{
    AdminToolDescriptors getAllowedAdminToolDescriptors( final PrincipalKeys principalKeys );

    AdminToolDescriptors getByApplication( final ApplicationKey applicationKey );

    AdminToolDescriptor getByKey( final DescriptorKey descriptorKey );

    String getIconByKey( DescriptorKey descriptorKey );

    String generateAdminToolUri( String application, String adminTool );

    String getHomeToolUri();
}
