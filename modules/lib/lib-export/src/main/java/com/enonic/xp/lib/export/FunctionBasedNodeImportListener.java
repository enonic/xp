package com.enonic.xp.lib.export;

import java.util.function.Function;

import com.enonic.xp.export.NodeImportListener;

import static java.util.Objects.requireNonNullElse;

public class FunctionBasedNodeImportListener
    implements NodeImportListener
{
    private static final Function<Integer, Void> NULL_FUNCTION = _ -> null;

    private final Function<Integer, Void> nodeImported;

    private final Function<Integer, Void> nodeResolved;

    private final Function<Integer, Void> nodeSkipped;

    public FunctionBasedNodeImportListener( final Function<Integer, Void> nodeImported, final Function<Integer, Void> nodeResolved,
                                            final Function<Integer, Void> nodeSkipped )
    {
        this.nodeImported = requireNonNullElse( nodeImported, NULL_FUNCTION );
        this.nodeResolved = requireNonNullElse( nodeResolved, NULL_FUNCTION );
        this.nodeSkipped = requireNonNullElse( nodeSkipped, NULL_FUNCTION );
    }

    @Override
    public void nodeImported( final int count )
    {
        nodeImported.apply( count );
    }

    @Override
    public void nodeResolved( final int count )
    {
        nodeResolved.apply( count );
    }

    @Override
    public void nodeSkipped( final int count )
    {
        nodeSkipped.apply( count );
    }
}
