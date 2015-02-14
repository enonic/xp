package com.enonic.xp.core.content;


import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

public class CreateMediaParams
{
    private ContentPath parent;

    private String name;

    private String mimeType;

    private ByteSource byteSource;

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

    public CreateMediaParams byteSource( final ByteSource value )
    {
        this.byteSource = value;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.parent, "parent cannot be null. Use ContentPath.ROOT when content has no parent." );
        Preconditions.checkArgument( this.parent.isAbsolute(), "parent must be absolute: " + this.parent );
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.byteSource, "byteSource cannot be null" );
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

    public ByteSource getByteSource()
    {
        return byteSource;
    }
}
