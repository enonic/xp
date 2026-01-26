package com.enonic.xp.lib.export;

import java.util.function.Function;

import com.enonic.xp.export.NodeImportListener;

import static java.util.Objects.requireNonNullElse;

public class FunctionBasedNodeImportListener
    implements NodeImportListener
{
    private static final Function<Long, Void> NULL_FUNCTION = aLong -> null;

    private final Function<Long, Void> nodeImported;

    private final Function<Long, Void> nodeResolved;

    private final Function<Long, Void> nodeSkipped;

    public FunctionBasedNodeImportListener( final Function<Long, Void> nodeImported, final Function<Long, Void> nodeResolved,
                                            final Function<Long, Void> nodeSkipped )
    {
        this.nodeImported = requireNonNullElse( nodeImported, NULL_FUNCTION );
        this.nodeResolved = requireNonNullElse( nodeResolved, NULL_FUNCTION );
        this.nodeSkipped = requireNonNullElse( nodeSkipped, NULL_FUNCTION );
    }

    @Override
    public void nodeImported( final long count )
    {
        nodeImported.apply( count );
    }

    @Override
    public void nodeResolved( final long count )
    {
        nodeResolved.apply( count );
    }

    @Override
    public void nodeSkipped( final long count )
    {
        nodeSkipped.apply( count );
    }
}
