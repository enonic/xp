package com.enonic.wem.api.command.content.type;

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

    public DeleteMixins delete()
    {
        return new DeleteMixins();
    }

}
