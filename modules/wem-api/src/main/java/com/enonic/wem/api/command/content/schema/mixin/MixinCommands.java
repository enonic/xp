package com.enonic.wem.api.command.content.schema.mixin;

public final class MixinCommands
{
    public GetMixins get()
    {
        return new GetMixins();
    }

    public CreateMixin create()
    {
        return new CreateMixin();
    }

    public UpdateMixins update()
    {
        return new UpdateMixins();
    }

    public DeleteMixin delete()
    {
        return new DeleteMixin();
    }

}
