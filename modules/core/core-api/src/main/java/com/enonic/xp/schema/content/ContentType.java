package com.enonic.xp.schema.content;


import java.util.List;

import com.google.common.base.MoreObjects;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;


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

    private final String displayNamePlaceholder;

    private final String displayNamePlaceholderI18nKey;

    private final String displayNameExpression;

    private final String displayNameListExpression;

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
        this.displayNamePlaceholder = builder.displayNamePlaceholder;
        this.displayNamePlaceholderI18nKey = builder.displayNamePlaceholderI18nKey;
        this.displayNameExpression = builder.displayNameExpression;
        this.displayNameListExpression = builder.displayNameListExpression;
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

    public String getDisplayNamePlaceholder()
    {
        return displayNamePlaceholder;
    }

    public String getDisplayNamePlaceholderI18nKey()
    {
        return displayNamePlaceholderI18nKey;
    }

    public String getDisplayNameExpression()
    {
        return displayNameExpression;
    }

    public String getDisplayNameListExpression()
    {
        return displayNameListExpression;
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        s.add( "name", getName() );
        s.add( "title", getTitle() );
        s.add( "description", getDescription() );
        s.add( "superType", superType );
        s.add( "isAbstract", isAbstract );
        s.add( "isFinal", isFinal );
        s.add( "isBuiltIn", isBuiltIn );
        s.add( "allowChildContent", allowChildContent );
        s.add( "form", form );
        s.add( "icon", getIcon() );
        s.add( "displayNamePlaceholder", getDisplayNamePlaceholder() );
        s.add( "displayNameExpression", getDisplayNameExpression() );
        s.add( "displayNameListExpression", getDisplayNameListExpression() );
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

        private String displayNamePlaceholder;

        private String displayNamePlaceholderI18nKey;

        private String displayNameExpression;

        private String displayNameListExpression;

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

        private Builder()
        {
            super();
            this.formBuilder = Form.create();
            this.isAbstract = false;
            this.isFinal = true;
            this.allowChildContent = true;
            this.isBuiltIn = false;
            this.allowChildContentType = List.of();
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
            this.displayNamePlaceholder = source.getDisplayNamePlaceholder();
            this.displayNamePlaceholderI18nKey = source.getDisplayNamePlaceholderI18nKey();
            this.displayNameExpression = source.getDisplayNameExpression();
            this.displayNameListExpression = source.getDisplayNameListExpression();

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

        public Builder setTitle( final LocalizedText source )
        {
            this.title( source.text() );
            this.titleI18nKey( source.i18n() );
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

        public Builder displayNamePlaceholder( final String displayNamePlaceholder )
        {
            this.displayNamePlaceholder = displayNamePlaceholder;
            return this;
        }

        public Builder displayNamePlaceholderI18nKey( final String displayNamePlaceholderI18nKey )
        {
            this.displayNamePlaceholderI18nKey = displayNamePlaceholderI18nKey;
            return this;
        }

        public Builder displayNamePlaceholder( final LocalizedText text )
        {
            this.displayNamePlaceholder = text.text();
            this.displayNamePlaceholderI18nKey = text.i18n();
            return this;
        }

        public Builder displayNameExpression( final String displayNameExpression )
        {
            this.displayNameExpression = displayNameExpression;
            return this;
        }

        public Builder displayNameListExpression( final String displayNameListExpression )
        {
            this.displayNameListExpression = displayNameListExpression;
            return this;
        }

        public ContentType build()
        {
            return new ContentType( this );
        }
    }
}
