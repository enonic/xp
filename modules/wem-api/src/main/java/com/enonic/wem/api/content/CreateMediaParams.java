package com.enonic.wem.api.content;


import java.io.InputStream;

import com.google.common.base.Preconditions;

public class CreateMediaParams
{
    private ContentPath parent;

    private String name;

    private String mimeType;

    private InputStream inputStream;

    public CreateMediaParams parent( final ContentPath value )
    {
        this.parent = value;
        return this;
    }

    public CreateMediaParams name( final String value )
    {
        this.name = value;
        return this;
    }

    public CreateMediaParams mimeType( final String value )
    {
        this.mimeType = value;
        return this;
    }

    public CreateMediaParams inputStream( final InputStream value )
    {
        this.inputStream = value;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.parent, "parent cannot be null. Use ContentPath.ROOT when content has no parent." );
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.mimeType, "mimeType cannot be null" );
        Preconditions.checkNotNull( this.inputStream, "inputStream cannot be null" );
    }

    public ContentPath getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }
}
