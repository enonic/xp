package com.enonic.xp.lib.export;

import java.util.function.Function;

import com.enonic.xp.export.NodeExportListener;

import static java.util.Objects.requireNonNullElse;

public class FunctionBasedNodeExportListener
    implements NodeExportListener
{
    private static final Function<Integer, Void> NULL_FUNCTION = aLong -> null;

    private final Function<Integer, Void> nodeExported;

    private final Function<Integer, Void> nodeResolved;

    public FunctionBasedNodeExportListener( final Function<Integer, Void> nodeExported, final Function<Integer, Void> nodeResolved )
    {
        this.nodeExported = requireNonNullElse( nodeExported, NULL_FUNCTION );
        this.nodeResolved = requireNonNullElse( nodeResolved, NULL_FUNCTION );
    }

    @Override
    public void nodeExported( final int count )
    {
        nodeExported.apply( count );
    }

    @Override
    public void nodeResolved( final int count )
    {
        nodeResolved.apply( count );
    }
}
