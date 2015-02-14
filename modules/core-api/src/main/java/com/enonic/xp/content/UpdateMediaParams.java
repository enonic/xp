package com.enonic.xp.content;


import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

public class UpdateMediaParams
{
    private ContentId content;

    private String name;

    private String mimeType;

    private ByteSource inputStream;

    public UpdateMediaParams content( final ContentId value )
    {
        this.content = value;
        return this;
    }

    public UpdateMediaParams name( final String value )
    {
        this.name = value;
        return this;
    }

    public UpdateMediaParams mimeType( final String value )
    {
        this.mimeType = value;
        return this;
    }

    public UpdateMediaParams byteSource( final ByteSource value )
    {
        this.inputStream = value;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.content, "content to update cannot be null." );
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.inputStream, "byteSource cannot be null" );
    }

    public ContentId getContent()
    {
        return content;
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
        return inputStream;
    }
}
