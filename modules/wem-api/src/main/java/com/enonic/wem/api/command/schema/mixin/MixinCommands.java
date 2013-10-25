package com.enonic.wem.api.command.schema.mixin;

public final class MixinCommands
{
    public GetMixinCommands get()
    {
        return new GetMixinCommands();
    }

    public CreateMixin create()
    {
        return new CreateMixin();
    }

    public UpdateMixin update()
    {
        return new UpdateMixin();
    }

    public DeleteMixin delete()
    {
        return new DeleteMixin();
    }

}
