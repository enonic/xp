package com.enonic.xp.repository;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.BinaryAttachment;

public final class EditableRepository
{
    public final Repository source;

    public PropertyTree data;

    public List<BinaryAttachment> binaryAttachments = new ArrayList<>();

    public EditableRepository( final Repository source )
    {
        this.source = source;
        this.data = source.getData().copy();
    }
}
