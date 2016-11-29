package com.enonic.xp.lib.value;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.BinaryAttachments;

public class ScriptValueTranslatorResult
{
    private final PropertyTree propertyTree;

    private final BinaryAttachments binaryAttachments;

    public ScriptValueTranslatorResult( final PropertyTree propertyTree, final BinaryAttachments binaryAttachments )
    {
        this.propertyTree = propertyTree;
        this.binaryAttachments = binaryAttachments;
    }

    public PropertyTree getPropertyTree()
    {
        return propertyTree;
    }

    public BinaryAttachments getBinaryAttachments()
    {
        return binaryAttachments;
    }
}