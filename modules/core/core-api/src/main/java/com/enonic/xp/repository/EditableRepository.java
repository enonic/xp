package com.enonic.xp.repository;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.BinaryAttachment;

public class EditableRepository
{
    public final Repository source;

    public PropertyTree data;

    public ImmutableList<BinaryAttachment> binaryAttachments = ImmutableList.of();

    public EditableRepository( final Repository source )
    {
        this.source = source;
        this.data = source.getData().getValue().copy();
    }
}
