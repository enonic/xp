package com.enonic.wem.api.command.content.attachment;

public final class AttachmentCommands
{
    public CreateAttachment create()
    {
        return new CreateAttachment();
    }

    public GetAttachment get()
    {
        return new GetAttachment();
    }

    public DeleteAttachment delete()
    {
        return new DeleteAttachment();
    }
}
