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

    private double focalX = 0.5;

    private double focalY = 0.5;

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

    public UpdateMediaParams focalX( final double focalX )
    {
        this.focalX = focalX;
        return this;
    }

    public UpdateMediaParams focalY( final double focalY )
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

    public double getFocalX()
    {
        return focalX;
    }

    public double getFocalY()
    {
        return focalY;
    }
}
