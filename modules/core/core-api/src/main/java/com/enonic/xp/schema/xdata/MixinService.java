package com.enonic.xp.schema.xdata;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public interface MixinService
{
    MixinDescriptor getByName( MixinName name );

    MixinDescriptors getByNames( MixinNames names );

    MixinDescriptors getAll();

    MixinDescriptors getByApplication( ApplicationKey applicationKey );
}
