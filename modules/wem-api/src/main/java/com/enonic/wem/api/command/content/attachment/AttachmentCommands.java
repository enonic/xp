package com.enonic.wem.api.command.content.attachment;

public final class AttachmentCommands
{
    public GetAttachment get()
    {
        return new GetAttachment();
    }

    public GetAttachments getAll()
    {
        return new GetAttachments();
    }
}
