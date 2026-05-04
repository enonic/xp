package com.enonic.xp.attachment;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.util.BinaryReference;

import static java.util.Objects.requireNonNull;


public final class CreateAttachment
{
    private final String mimeType;

    private final String name;

    private final String label;

    private final ByteSource byteSource;

    private CreateAttachment( final Builder builder )
    {
        this.mimeType = builder.mimeType;
        this.name = builder.name;
        this.label = builder.label;
        this.byteSource = builder.byteSource;
    }

    public String getName()
    {
        return name;
    }

    public String getNameWithoutExtension()
    {
        return Files.getNameWithoutExtension( name );
    }

    public String getExtension()
    {
        return Files.getFileExtension( name );
    }

    public String getLabel()
    {
        return label;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }

    public BinaryReference getBinaryReference()
    {
        return BinaryReference.from( name );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final CreateAttachment source )
    {
        return new Builder( source );
    }

    public static final class Builder
    {
        private ByteSource byteSource;

        private String mimeType;

        private String name;

        private String label;

        private Builder()
        {
        }

        public Builder( final CreateAttachment source )
        {
            this.name = source.name;
            this.mimeType = source.mimeType;
            this.label = source.label;
            this.byteSource = source.byteSource;
        }

        public Builder mimeType( final String value )
        {
            mimeType = value;
            return this;
        }

        public Builder name( final String value )
        {
            name = value;
            return this;
        }

        public Builder label( final String value )
        {
            label = value;
            return this;
        }

        public Builder byteSource( final ByteSource value )
        {
            byteSource = value;
            return this;
        }

        private void validate()
        {
            requireNonNull( name, "name is required" );
            requireNonNull( byteSource, "byteSource is required" );
        }

        public CreateAttachment build()
        {
            this.validate();
            return new CreateAttachment( this );
        }
    }
}
