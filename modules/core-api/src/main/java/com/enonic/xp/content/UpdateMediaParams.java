package com.enonic.xp.content;


import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

@Beta
public class UpdateMediaParams
{
    private ContentId content;

    private String name;

    private String mimeType;

    private ByteSource inputStream;

    private Double focalX = 0.5;

    private Double focalY = 0.5;

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

    public UpdateMediaParams focalX( final Double focalX )
    {
        this.focalX = focalX;
        return this;
    }

    public UpdateMediaParams focalY( final Double focalY )
    {
        this.focalY = focalY;
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

    public Double getFocalX()
    {
        return focalX;
    }

    public Double getFocalY()
    {
        return focalY;
    }
}
