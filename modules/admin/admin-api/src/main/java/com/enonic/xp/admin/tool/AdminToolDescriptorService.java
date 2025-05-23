package com.enonic.xp.admin.tool;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

@PublicApi
public interface AdminToolDescriptorService
{
    AdminToolDescriptors getAllowedAdminToolDescriptors( PrincipalKeys principalKeys );

    AdminToolDescriptors getByApplication( ApplicationKey applicationKey );

    AdminToolDescriptor getByKey( DescriptorKey descriptorKey );

    String getIconByKey( DescriptorKey descriptorKey );
}
