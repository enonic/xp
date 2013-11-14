package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;

public interface ContentEditor
{
    public Content.EditBuilder edit( Content toBeEdited );
}
