package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentEditor;
import com.enonic.wem.api.content.attachment.CreateAttachments;


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
