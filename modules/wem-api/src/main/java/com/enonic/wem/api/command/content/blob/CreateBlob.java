package com.enonic.wem.api.command.content.blob;


import java.io.InputStream;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.command.Command;

public final class CreateBlob
    extends Command<Blob>
{
    private InputStream byteSource;

    public CreateBlob( final InputStream byteSource )
    {
        this.byteSource = byteSource;
    }

    public InputStream getInputStream()
    {
        return byteSource;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateBlob ) )
        {
            return false;
        }

        final CreateBlob that = (CreateBlob) o;
        return Objects.equal( this.byteSource, that.byteSource );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.byteSource );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.byteSource, "byteSource cannot be null" );
    }

}
