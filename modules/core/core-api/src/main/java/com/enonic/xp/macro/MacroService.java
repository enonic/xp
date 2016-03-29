package com.enonic.xp.macro;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

@Beta
public interface MacroService
{
    MacroDescriptor getByKey( final MacroKey key );

    MacroDescriptors getByApplication( final ApplicationKey applicationKey );

    MacroDescriptors getByApplications( final ApplicationKeys applicationKeys );
}
