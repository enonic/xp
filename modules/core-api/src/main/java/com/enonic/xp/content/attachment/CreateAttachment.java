package com.enonic.xp.content.attachment;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.util.BinaryReference;


public class CreateAttachment
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

    public static class Builder
    {
        private ByteSource byteSource;

        private String mimeType;

        private String name;

        private String label;

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
            Preconditions.checkNotNull( name );
            Preconditions.checkNotNull( byteSource );
        }

        public CreateAttachment build()
        {
            this.validate();
            return new CreateAttachment( this );
        }
    }
}
