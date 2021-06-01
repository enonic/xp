package com.enonic.xp.lib.export;

import java.util.function.Function;

import com.enonic.xp.export.NodeExportListener;

import static java.util.Objects.requireNonNullElse;

public class FunctionBasedNodeExportListener
    implements NodeExportListener
{
    private static final Function<Long, Void> NULL_FUNCTION = aLong -> null;

    private final Function<Long, Void> nodeExported;

    private final Function<Long, Void> nodeResolved;

    public FunctionBasedNodeExportListener( final Function<Long, Void> nodeExported, final Function<Long, Void> nodeResolved )
    {
        this.nodeExported = requireNonNullElse( nodeExported, NULL_FUNCTION );
        this.nodeResolved = requireNonNullElse( nodeResolved, NULL_FUNCTION );
    }

    @Override
    public void nodeExported( final long count )
    {
        nodeExported.apply( count );
    }

    @Override
    public void nodeResolved( final long count )
    {
        nodeResolved.apply( count );
    }
}
