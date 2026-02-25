package com.enonic.xp.macro;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public interface MacroDescriptorService
{
    MacroDescriptor getByKey( MacroKey key );

    MacroDescriptors getByApplication( ApplicationKey applicationKey );

    MacroDescriptors getByApplications( ApplicationKeys applicationKeys );

    MacroDescriptors getAll();

    ResourceKey getControllerResourceKey( MacroKey key );

    ResourceKey getDescriptorResourceKey( MacroKey key );
}
