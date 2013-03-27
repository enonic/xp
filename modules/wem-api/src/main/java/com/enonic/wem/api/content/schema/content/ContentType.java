package com.enonic.wem.api.content.schema.content;


import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.schema.Schema;
import com.enonic.wem.api.content.schema.SchemaKey;
import com.enonic.wem.api.content.schema.content.form.Form;
import com.enonic.wem.api.content.schema.content.form.FormItem;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.schema.content.form.Form.newForm;

public final class ContentType
    implements Schema
{
    private final String name;

    private final String displayName;

    private final QualifiedContentTypeName superType;

    private final boolean isAbstract;

    private final boolean isFinal;

    private final ModuleName moduleName;

    private final Form form;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final Icon icon;

    private final String contentDisplayNameScript;

    private ContentType( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "Name cannot be null in ContentType" );
        Preconditions.checkNotNull( builder.moduleName, "Module name cannot be null in ContentType" );
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.superType = builder.superType;
        this.isAbstract = builder.isAbstract;
        this.isFinal = builder.isFinal;
        this.moduleName = builder.moduleName;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.form = builder.formBuilder.build();
        this.icon = builder.icon;
        this.contentDisplayNameScript = builder.contentDisplayNameScript;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public QualifiedContentTypeName getQualifiedName()
    {
        return new QualifiedContentTypeName( moduleName, name );
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

    @Override
    public ModuleName getModuleName()
    {
        return moduleName;
    }

    @Override
    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    @Override
    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public Form form()
    {
        return this.form;
    }

    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public SchemaKey getSchemaKey()
    {
        return SchemaKey.from( getQualifiedName() );
    }

    public String getContentDisplayNameScript()
    {
        return contentDisplayNameScript;
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "name", name );
        s.add( "displayName", displayName );
        s.add( "module", moduleName.toString() );
        s.add( "superType", superType );
        s.add( "isAbstract", isAbstract );
        s.add( "isFinal", isFinal );
        s.add( "form", form );
        s.add( "icon", icon );
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
    {
        private String name;

        private ModuleName moduleName;

        private String displayName;

        private boolean isAbstract;

        private boolean isFinal;

        private Form.Builder formBuilder = newForm();

        private QualifiedContentTypeName superType;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private Icon icon;

        private String contentDisplayNameScript;

        private Builder()
        {
            formBuilder = newForm();
        }

        private Builder( final ContentType source )
        {
            this.name = source.getName();
            this.moduleName = source.getModuleName();
            this.displayName = source.getDisplayName();
            this.isAbstract = source.isAbstract();
            this.isFinal = source.isFinal();
            this.superType = source.getSuperType();
            if ( source.form() != null )
            {
                this.formBuilder = newForm( source.form() );
            }
            this.createdTime = source.createdTime;
            this.modifiedTime = source.modifiedTime;
            this.contentDisplayNameScript = source.contentDisplayNameScript;
            this.icon = source.icon;
        }

        public Builder qualifiedName( final QualifiedContentTypeName qualifiedContentTypeName )
        {
            this.name = qualifiedContentTypeName.getContentTypeName();
            this.moduleName = qualifiedContentTypeName.getModuleName();
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder module( final ModuleName moduleName )
        {
            this.moduleName = moduleName;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
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

        public Builder superType( final QualifiedContentTypeName superType )
        {
            this.superType = superType;
            return this;
        }

        public Builder createdTime( final DateTime value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder modifiedTime( final DateTime value )
        {
            this.modifiedTime = value;
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

        public Builder icon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder contentDisplayNameScript( final String contentDisplayNameScript )
        {
            this.contentDisplayNameScript = contentDisplayNameScript;
            return this;
        }

        public ContentType build()
        {
            if ( superType == null && ( moduleName != null && !moduleName.equals( Module.SYSTEM.getName() ) ) )
            {
                superType = QualifiedContentTypeName.unstructured();
            }

            return new ContentType( this );
        }
    }
}
