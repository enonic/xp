package com.enonic.xp.content.attachment;

import org.apache.commons.io.FilenameUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.xp.util.BinaryReference;

@Beta
public final class Attachment
{
    private final long size;

    private final String mimeType;

    private final String name;

    private final String label;

    public Attachment( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name is mandatory for an Attachment" );
        Preconditions.checkNotNull( builder.mimeType, "mimeType is mandatory for an Attachment" );
        Preconditions.checkNotNull( builder.size, "size is mandatory for an Attachment" );

        this.mimeType = builder.mimeType;
        this.name = builder.name;
        this.size = builder.size;
        this.label = builder.label;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public String getName()
    {
        return name;
    }

    public BinaryReference getBinaryReference()
    {
        return BinaryReference.from( name );
    }

    public String getNameWithoutExtension()
    {
        return FilenameUtils.getBaseName( name );
    }

    public String getExtension()
    {
        return FilenameUtils.getExtension( name );
    }

    public String getLabel()
    {
        return label;
    }

    public long getSize()
    {
        return size;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Attachment ) )
        {
            return false;
        }
        final Attachment that = (Attachment) o;
        return Objects.equal( this.name, that.name ) &&
            Objects.equal( this.mimeType, that.mimeType ) &&
            Objects.equal( this.label, that.label ) &&
            Objects.equal( this.size, that.size );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, mimeType, label, size );
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        s.add( "name", name );
        s.add( "mimeType", mimeType );
        s.add( "label", label );
        s.add( "size", size );
        return s.toString();
    }

    public static Builder newAttachment()
    {
        return new Builder();
    }

    public static Builder newAttachment( final Attachment source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private String mimeType;

        private String name;

        private String label;

        private long size;

        private Builder()
        {

        }

        private Builder( final Attachment attachment )
        {
            this.mimeType = attachment.mimeType;
            this.name = attachment.name;
            this.label = attachment.label;
            this.size = attachment.size;
        }

        public Builder mimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder label( final String label )
        {
            this.label = label;
            return this;
        }

        public Builder size( final long size )
        {
            this.size = size;
            return this;
        }

        public Attachment build()
        {
            return new Attachment( this );
        }
    }
}
