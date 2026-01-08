package com.enonic.xp.content;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public interface ContentMetadataEditor
{
    void edit( EditableContentMetadata edit );
}
