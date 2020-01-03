package com.enonic.xp.macro;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

@PublicApi
public interface MacroDescriptorService
{
    MacroDescriptor getByKey( final MacroKey key );

    MacroDescriptors getByApplication( final ApplicationKey applicationKey );

    MacroDescriptors getByApplications( final ApplicationKeys applicationKeys );

    MacroDescriptors getAll();
}
