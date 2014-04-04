package com.enonic.wem.api.schema.mixin;

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

