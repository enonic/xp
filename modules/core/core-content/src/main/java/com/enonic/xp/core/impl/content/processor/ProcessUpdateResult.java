package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;


public final class ProcessUpdateResult
{
    final ContentEditor editor;

    final CreateAttachments createAttachments;

    public ProcessUpdateResult( final CreateAttachments createAttachments, final ContentEditor editor )
    {
        this.createAttachments = createAttachments;
        this.editor = editor;
    }

    public ContentEditor getEditor()
    {
        return editor;
    }

    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
    }
}
