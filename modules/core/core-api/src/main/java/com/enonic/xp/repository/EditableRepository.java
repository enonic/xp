package com.enonic.xp.repository;

import com.enonic.xp.data.PropertyTree;

public class EditableRepository
{
    public final Repository source;

    public PropertyTree data;

    public RepositoryBinaryAttachments binaryAttachments = RepositoryBinaryAttachments.empty();

    public EditableRepository( final Repository source )
    {
        this.source = source;
        this.data = source.getData().getValue().copy();
    }
}
