package com.enonic.wem.api.command.schema.mixin;

import com.enonic.wem.api.schema.mixin.Mixin;

public class UpdateMixinResult
{
    private final Mixin persistedMixin;

    public UpdateMixinResult( final Mixin persistedMixin )
    {
        this.persistedMixin = persistedMixin;
    }

    public Mixin getPersistedMixin()
    {
        return persistedMixin;
    }
}

