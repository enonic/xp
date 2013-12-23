package com.enonic.wem.api.command.schema.mixin;


import com.enonic.wem.api.schema.mixin.Mixin;

public class DeleteMixinResult
{
    public final Mixin deletedMixin;

    public DeleteMixinResult( final Mixin deletedMixin )
    {
        this.deletedMixin = deletedMixin;
    }
}
