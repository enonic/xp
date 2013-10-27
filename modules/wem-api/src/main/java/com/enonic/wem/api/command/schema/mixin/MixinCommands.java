package com.enonic.wem.api.command.schema.mixin;

public final class MixinCommands
{
    public MixinGetCommands get()
    {
        return new MixinGetCommands();
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
