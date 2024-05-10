package com.enonic.xp.repo.impl.node.json;

import java.util.List;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;

public final class ImmutableNodeVersion
{
    public final NodeId id;

    public final NodeType nodeType;

    public final List<ImmutableProperty> data;

    public final ChildOrder childOrder;

    public final Long manualOrderValue;

    public final AttachedBinaries attachedBinaries;

    public ImmutableNodeVersion( final NodeId id, final NodeType nodeType, final List<ImmutableProperty> data,
                                 final ChildOrder childOrder,
                                 final Long manualOrderValue,
                                 final AttachedBinaries attachedBinaries )
    {
        this.id = id;
        this.nodeType = nodeType;
        this.data = List.copyOf( data );
        this.childOrder = childOrder;
        this.manualOrderValue = manualOrderValue;
        this.attachedBinaries = attachedBinaries;
    }
}
