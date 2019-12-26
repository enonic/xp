package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface NodeEditor
{
    void edit( EditableNode toBeEdited );
}
