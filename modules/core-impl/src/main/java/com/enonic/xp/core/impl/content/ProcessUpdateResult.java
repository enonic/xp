package com.enonic.xp.core.impl.content;

import com.enonic.xp.core.content.ContentEditor;
import com.enonic.xp.core.content.attachment.CreateAttachments;


class ProcessUpdateResult
{
    final ContentEditor editor;

    final CreateAttachments createAttachments;

    ProcessUpdateResult( final CreateAttachments createAttachments, final ContentEditor editor )
    {
        this.createAttachments = createAttachments;
        this.editor = editor;
    }
}
