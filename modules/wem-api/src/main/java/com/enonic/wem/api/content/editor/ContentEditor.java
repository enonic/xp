package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;

public interface ContentEditor<C extends Content>
{
    public Content.EditBuilder edit( C toBeEdited );
}
