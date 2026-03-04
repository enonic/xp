package com.enonic.xp.schema.mixin;

import com.enonic.xp.app.ApplicationKey;


public interface MixinService
{
    MixinDescriptor getByName( MixinName name );

    MixinDescriptors getByNames( MixinNames names );

    MixinDescriptors getAll();

    MixinDescriptors getByApplication( ApplicationKey applicationKey );
}
