package com.enonic.xp.node;

import com.enonic.xp.data.PropertyTree;

public interface NodeDataProcessor
{
    PropertyTree process( final PropertyTree originalData );
}
