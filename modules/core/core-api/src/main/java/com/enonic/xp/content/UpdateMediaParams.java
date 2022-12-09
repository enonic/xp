package com.enonic.xp.content;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UpdateMediaParams
{
    private ContentId content;

    private String name;

    private String mimeType;

    private ByteSource inputStream;

    private double focalX = 0.5;

    private double focalY = 0.5;

    private String caption;

    private List<String> artist = List.of();

    private String copyright;

    private List<String> tags = List.of();

    private WorkflowInfo workflowInfo;

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

    public UpdateMediaParams caption( final String caption )
    {
        this.caption = caption;
        return this;
    }

    @Deprecated
    public UpdateMediaParams artist( final String artist )
    {
        this.artist = artist != null ? List.of( artist ) : List.of();
        return this;
    }

    public UpdateMediaParams artist( final List<String> artist )
    {
        this.artist = List.copyOf( artist );
        return this;
    }

    public UpdateMediaParams copyright( final String copyright )
    {
        this.copyright = copyright;
        return this;
    }

    @Deprecated
    public UpdateMediaParams tags( final String tags )
    {
        this.tags = tags != null ? List.of( tags ) : List.of();
        return this;
    }

    public UpdateMediaParams tags( final List<String> tags )
    {
        this.tags = List.copyOf( tags );
        return this;
    }

    public UpdateMediaParams workflowInfo( final WorkflowInfo workflowInfo )
    {
        this.workflowInfo = workflowInfo;
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

    public String getCaption()
    {
        return caption;
    }

    public List<String> getArtistList()
    {
        return artist;
    }

    @Deprecated
    public String getArtist()
    {
        return !artist.isEmpty() ? artist.get( 0 ) : null;
    }

    public String getCopyright()
    {
        return copyright;
    }

    public List<String> getTagList()
    {
        return tags;
    }

    @Deprecated
    public String getTags()
    {
        return !tags.isEmpty() ? tags.get( 0 ) : null;
    }

    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
