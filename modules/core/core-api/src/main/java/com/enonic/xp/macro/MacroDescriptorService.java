package com.enonic.xp.macro;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

@PublicApi
public interface MacroDescriptorService
{
    MacroDescriptor getByKey( MacroKey key );

    MacroDescriptors getByApplication( ApplicationKey applicationKey );

    MacroDescriptors getByApplications( ApplicationKeys applicationKeys );

    MacroDescriptors getAll();
}
