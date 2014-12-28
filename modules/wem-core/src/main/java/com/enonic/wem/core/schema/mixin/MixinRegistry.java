package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;

public interface MixinRegistry
{

    Mixin getMixin( MixinName mixinName );

    Mixins getMixinsByModule( ModuleKey moduleKey );

    Mixins getAllMixins();

}
