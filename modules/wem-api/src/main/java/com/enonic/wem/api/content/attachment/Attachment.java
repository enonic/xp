package com.enonic.wem.api.content.attachment;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.binary.Binary;

public final class Attachment
{
    private final Binary binary;

    private final String mimeType;

    private final String name;

    private final String label;

    public Attachment( final Builder builder )
    {
        this.binary = builder.binary;
        this.mimeType = builder.mimeType;
        this.name = builder.name;
        this.label = builder.label;
    }

    public Binary getBinary()
    {
        return binary;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public long getSize()
    {
        return binary.getSize();
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
            Objects.equal( this.binary, that.binary );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, mimeType, label, binary );
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "name", name );
        s.add( "mimeType", mimeType );
        s.add( "label", label );
        s.add( "binary", binary );
        return s.toString();
    }

    public static Builder newAttachment()
    {
        return new Builder();
    }

    public static Builder newAttachment( final Attachment space )
    {
        return new Builder( space );
    }

    public static class Builder
    {
        private Binary binary;

        private String mimeType;

        private String name;

        private String label;

        private Builder()
        {
            this.binary = null;
            this.mimeType = null;
            this.name = null;
            this.label = null;
        }

        private Builder( final Attachment attachment )
        {
            this.binary = attachment.binary;
            this.mimeType = attachment.mimeType;
            this.name = attachment.name;
            this.label = attachment.label;
        }

        public Builder binary( final Binary binary )
        {
            this.binary = binary;
            return this;
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

        public Attachment build()
        {
            Preconditions.checkNotNull( name, "name is mandatory for an attachment" );
            Preconditions.checkNotNull( mimeType, "mimeType is mandatory for an attachment" );
            Preconditions.checkNotNull( binary, "binary is mandatory for an attachment" );
            return new Attachment( this );
        }
    }
}
