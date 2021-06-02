package com.enonic.xp.admin.impl.rest;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class LimitingInputStream<X extends RuntimeException>
    extends FilterInputStream
{
    private final long limit;

    private final Supplier<? extends X> exceptionSupplier;

    private long total;

    public LimitingInputStream( final InputStream in, final long limit, Supplier<? extends X> exceptionSupplier )
    {
        super( in );
        this.limit = limit;
        this.exceptionSupplier = exceptionSupplier;
    }

    @Override
    public int read()
        throws IOException
    {
        int i = in.read();
        if ( i >= 0 )
        {
            throwIfExceeded( 1 );
        }
        return i;
    }

    @Override
    public int read( final byte[] b, final int off, final int len )
        throws IOException
    {
        int i = in.read( b, off, len );
        if ( i >= 0 )
        {
            throwIfExceeded( i );
        }
        return i;
    }

    private void throwIfExceeded( final int size )
        throws X
    {
        total += size;
        if ( total > limit )
        {
            throw exceptionSupplier.get();
        }
    }
}
