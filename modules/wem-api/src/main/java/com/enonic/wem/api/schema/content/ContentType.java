package com.enonic.wem.api.schema.content;


import com.google.common.base.Objects;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static com.enonic.wem.api.form.Form.newForm;

public final class ContentType
    extends BaseSchema<ContentTypeName>
    implements Schema, IllegalEditAware<ContentType>
{
    private final ContentTypeName superType;

    private final boolean isAbstract;

    private final boolean isFinal;

    private final boolean allowChildContent;

    private final boolean isBuiltIn;

    private final Form form;

    private final String contentDisplayNameScript;

    private final boolean hasInheritors;

    private ContentType( final Builder builder )
    {
        super( builder );

        if ( builder.superType == null && !builder.isBuiltIn )
        {
            superType = ContentTypeName.unstructured();
        }
        else
        {
            this.superType = builder.superType;
        }
        this.hasInheritors = builder.inheritors;
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
    public ContentTypeName getQualifiedName()
    {
        return ContentTypeName.from( getName() );
    }

    @Override
    public boolean hasChildren()
    {
        return hasInheritors();
    }

    public boolean hasInheritors()
    {
        return this.hasInheritors;
    }

    public boolean inherit( final ContentTypeName contentType )
    {
        return this.superType != null && this.superType.equals( contentType );
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

    public Form form()
    {
        return this.form;
    }

    public String getContentDisplayNameScript()
    {
        return contentDisplayNameScript;
    }

    @Override
    public void checkIllegalEdit( final ContentType to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.getId(), to.getId(), ContentType.class );
        IllegalEdit.check( "schemaKey", this.getSchemaKey(), to.getSchemaKey(), ContentType.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), ContentType.class );
        IllegalEdit.check( "creator", this.getCreator(), to.getCreator(), ContentType.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), ContentType.class );
        IllegalEdit.check( "modifier", this.getModifier(), to.getModifier(), ContentType.class );
        IllegalEdit.check( "inheritors", this.hasInheritors(), to.hasInheritors(), ContentType.class );
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

        private ContentTypeName superType;

        private String contentDisplayNameScript;

        private boolean inheritors;

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
            this.inheritors = source.hasInheritors();
            this.superType = source.getSuperType();
            if ( source.form() != null )
            {
                this.formBuilder = newForm( source.form() );
            }
            this.contentDisplayNameScript = source.getContentDisplayNameScript();
        }

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
            this.formBuilder = newForm( form );
            return this;
        }

        public Builder inheritors( final boolean value )
        {
            this.inheritors = value;
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
