package com.enonic.xp.content.processor;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;


public final class ProcessUpdateResult
{
    final ContentEditor editor;

    final CreateAttachments createAttachments;

    @Deprecated
    public ProcessUpdateResult( final CreateAttachments createAttachments, final ContentEditor editor )
    {
        this( editor );
    }

    public ProcessUpdateResult( final ContentEditor editor )
    {
        this.editor = editor;
        this.createAttachments = CreateAttachments.empty();
    }

    public ContentEditor getEditor()
    {
        return editor;
    }

    @Deprecated
    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
    }
}
