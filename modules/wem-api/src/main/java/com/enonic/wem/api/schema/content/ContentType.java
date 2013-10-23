package com.enonic.wem.api.schema.content;


import com.google.common.base.Objects;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKey;

import static com.enonic.wem.api.form.Form.newForm;

public final class ContentType
    extends BaseSchema<QualifiedContentTypeName>
    implements Schema
{
    private final QualifiedContentTypeName superType;

    private final boolean isAbstract;

    private final boolean isFinal;

    private final boolean allowChildContent;

    private final boolean isBuiltIn;

    private final Form form;

    private final String contentDisplayNameScript;

    private ContentType( final Builder builder )
    {
        super( builder );

        if ( builder.superType == null && !builder.isBuiltIn )
        {
            superType = QualifiedContentTypeName.unstructured();
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
        this.contentDisplayNameScript = builder.contentDisplayNameScript;
    }

    @Override
    public SchemaKey getSchemaKey()
    {
        return SchemaKey.from( getQualifiedName() );
    }

    @Override
    public QualifiedContentTypeName getQualifiedName()
    {
        return QualifiedContentTypeName.from( getName() );
    }

    public QualifiedContentTypeName getSuperType()
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

    public Form form()
    {
        return this.form;
    }

    public String getContentDisplayNameScript()
    {
        return contentDisplayNameScript;
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "name", getName() );
        s.add( "displayName", getDisplayName() );
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

    public static Builder newContentType()
    {
        return new Builder();
    }

    public static Builder newContentType( final ContentType contentType )
    {
        return new Builder( contentType );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder>
    {
        private boolean isAbstract;

        private boolean isFinal;

        private boolean allowChildContent;

        private boolean isBuiltIn;

        private Form.Builder formBuilder = newForm();

        private QualifiedContentTypeName superType;

        private String contentDisplayNameScript;

        private Builder()
        {
            super();
            formBuilder = newForm();
            allowChildContent = true;
            isBuiltIn = false;
        }

        private Builder( final ContentType source )
        {
            super( source );
            this.isAbstract = source.isAbstract();
            this.isFinal = source.isFinal();
            this.allowChildContent = source.allowChildContent();
            this.isBuiltIn = source.isBuiltIn();

            this.superType = source.getSuperType();
            if ( source.form() != null )
            {
                this.formBuilder = newForm( source.form() );
            }
            this.contentDisplayNameScript = source.contentDisplayNameScript;
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

        public Builder setFinal( final boolean aFinal )
        {
            isFinal = aFinal;
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

        public Builder builtIn( final boolean builtIn )
        {
            isBuiltIn = builtIn;
            return this;
        }

        public Builder superType( final QualifiedContentTypeName superType )
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
            this.formBuilder = newForm( form );
            return this;
        }

        public Builder contentDisplayNameScript( final String contentDisplayNameScript )
        {
            this.contentDisplayNameScript = contentDisplayNameScript;
            return this;
        }

        public ContentType build()
        {
            return new ContentType( this );
        }
    }
}
