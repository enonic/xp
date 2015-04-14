package com.enonic.xp.schema.mixin;

import java.util.function.Supplier;

import com.google.common.annotations.Beta;

@Beta
public interface MixinProvider
    extends Supplier<Mixins>
{
}
