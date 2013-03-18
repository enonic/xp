package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.JavaType;

public class ContentReference
    extends Data
{
    public ContentReference( final String name, final ContentId value )
    {
        super( newContentReferenceBuilder().name( name ).value( value ) );
    }

    ContentReference( final Builder builder )
    {
        super( builder );
    }

    public static Builder newContentReferenceBuilder()
    {
        return new Builder();
    }


    public static class Builder
        extends BaseBuilder<Builder>
    {
        public Builder()
        {
            setType( DataTypes.CONTENT_REFERENCE );
        }

        public Builder value( final ContentId value )
        {
            setValue( value );
            return this;
        }

        public Builder value( final String value )
        {
            setValue( JavaType.CONTENT_ID.convertFrom( value ) );
            return this;
        }

        @Override
        public ContentReference build()
        {
            return new ContentReference( this );
        }
    }
}
