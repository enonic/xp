package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentIds;


public final class ProcessUpdateResult
{
    final ContentEditor editor;

    final ContentIds processedReferences;

    public ProcessUpdateResult( final ContentEditor editor, final ContentIds processedReferences )
    {
        this.editor = editor;
        this.processedReferences = processedReferences;
    }

    public ContentEditor getEditor()
    {
        return editor;
    }

    public ContentIds getProcessedReferences()
    {
        return processedReferences;
    }
}
