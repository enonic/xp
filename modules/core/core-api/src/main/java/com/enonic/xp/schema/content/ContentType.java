package com.enonic.xp.schema.content;


import java.util.List;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

@PublicApi
public final class ContentType
    extends BaseSchema<ContentTypeName>
{
    private final ContentTypeName superType;

    private final boolean isAbstract;

    private final boolean isFinal;

    private final boolean allowChildContent;

    private final boolean isBuiltIn;

    private final Form form;

    private final GenericValue schemaConfig;

    private final List<String> allowChildContentType;

    ContentType( final Builder builder )
    {
        super( builder );

        if ( builder.superType == null && !builder.isBuiltIn )
        {
            throw new IllegalArgumentException( "Non-built-in content types must have a super type defined" );
        }
        else
        {
            this.superType = builder.superType;
        }
        this.isAbstract = builder.isAbstract;
        this.isFinal = builder.isFinal;
        this.allowChildContent = builder.allowChildContent;
        this.isBuiltIn = builder.isBuiltIn;
        this.form = builder.formBuilder.build();
        this.schemaConfig = builder.schemaConfig.build();
        this.allowChildContentType = builder.allowChildContentType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ContentType contentType )
    {
        return new Builder( contentType );
    }

    public boolean hasSuperType()
    {
        return superType != null;
    }

    public ContentTypeName getSuperType()
    {
        return superType;
    }

    public boolean isAbstract()
    {
        return isAbstract;
    }

    public boolean isFinal()
    {
        return isFinal;
    }

    public boolean allowChildContent()
    {
        return allowChildContent;
    }

    public boolean isBuiltIn()
    {
        return isBuiltIn;
    }

    public Form getForm()
    {
        return this.form;
    }

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
    }

    public List<String> getAllowChildContentType()
    {
        return allowChildContentType;
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        s.add( "name", getName() );
        s.add( "displayName", getDisplayName() );
        s.add( "description", getDescription() );
        s.add( "superType", superType );
        s.add( "isAbstract", isAbstract );
        s.add( "isFinal", isFinal );
        s.add( "isBuiltIn", isBuiltIn );
        s.add( "allowChildContent", allowChildContent );
        s.add( "form", form );
        s.add( "icon", getIcon() );
        s.omitNullValues();
        return s.toString();
    }

    public static final class Builder
        extends BaseSchema.Builder<Builder, ContentTypeName>
    {
        private boolean isAbstract;

        private boolean isFinal;

        private boolean allowChildContent;

        private boolean isBuiltIn;

        private Form.Builder formBuilder;

        private ContentTypeName superType;

        private List<String> allowChildContentType;

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

        private Builder()
        {
            super();
            formBuilder = Form.create();
            isAbstract = false;
            isFinal = true;
            allowChildContent = true;
            isBuiltIn = false;
            allowChildContentType = List.of();
        }

        private Builder( final ContentType source )
        {
            super( source );
            this.isAbstract = source.isAbstract();
            this.isFinal = source.isFinal();
            this.allowChildContent = source.allowChildContent();
            this.isBuiltIn = source.isBuiltIn();
            this.superType = source.getSuperType();
            this.formBuilder = Form.create( source.getForm() );
            this.allowChildContentType = source.allowChildContentType;

            if ( source.schemaConfig != null )
            {
                source.schemaConfig.properties().forEach( p -> this.schemaConfig.put( p.getKey(), p.getValue() ) );
            }
        }

        @Override
        public Builder name( final ContentTypeName value )
        {
            super.name( value );
            return this;
        }

        public Builder name( final String value )
        {
            super.name( ContentTypeName.from( value ) );
            return this;
        }

        public Builder setAbstract( final boolean value )
        {
            isAbstract = value;
            return this;
        }

        public Builder setAbstract()
        {
            isAbstract = true;
            return this;
        }

        public Builder setFinal( final boolean value )
        {
            isFinal = value;
            return this;
        }

        public Builder setFinal()
        {
            isFinal = true;
            return this;
        }

        public Builder allowChildContent( final boolean value )
        {
            this.allowChildContent = value;
            return this;
        }

        public Builder setBuiltIn( final boolean builtIn )
        {
            isBuiltIn = builtIn;
            return this;
        }

        public Builder setBuiltIn()
        {
            return setBuiltIn( true );
        }

        public Builder superType( final ContentTypeName superType )
        {
            this.superType = superType;
            return this;
        }

        public Builder addFormItem( final FormItem formItem )
        {
            this.formBuilder.addFormItem( formItem );
            return this;
        }

        public Builder form( final Form form )
        {
            this.formBuilder = form != null ? Form.create( form ) : Form.create();
            return this;
        }

        public Builder setDisplayName( final LocalizedText source )
        {
            this.displayName( source.text() );
            this.displayNameI18nKey( source.i18n() );
            return this;
        }

        public Builder setDescription( final LocalizedText source )
        {
            this.description( source.text() );
            this.descriptionI18nKey( source.i18n() );
            return this;
        }

        public Builder schemaConfig( final GenericValue config )
        {
            config.properties().forEach( e -> this.schemaConfig.put( e.getKey(), e.getValue() ) );
            return this;
        }

        public Builder allowChildContentType( final List<String> allowChildContentType )
        {
            this.allowChildContentType = List.copyOf( allowChildContentType );
            return this;
        }

        public ContentType build()
        {
            return new ContentType( this );
        }
    }
}
