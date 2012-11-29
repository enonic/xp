package com.enonic.wem.api.content.type;


import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.type.form.Form;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.type.form.Form.newForm;

public final class ContentType
{
    private final String name;

    private final String displayName;

    private final QualifiedContentTypeName superType;

//    private final ContentHandler contentHandler;

    private final boolean isAbstract;

    private final boolean isFinal;

    private final ModuleName moduleName;

//    private final ComputedDisplayName computedDisplayName;

    private final Form form;

    private ContentType( final String name, final String displayName, final QualifiedContentTypeName superType, final boolean isAbstract,
                         final boolean isFinal, final ModuleName moduleName, final Form form )
    {
        this.name = name;
        this.displayName = displayName;
        this.superType = superType;
        this.isAbstract = isAbstract;
        this.isFinal = isFinal;
        this.moduleName = moduleName;
        this.form = form;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

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

    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public Form form()
    {
        return this.form;
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

        private final List<FormItem> formItemList;

        private QualifiedContentTypeName superType;

        private Builder()
        {
            formItemList = Lists.newArrayList();
        }

        private Builder( final ContentType contentType )
        {
            this.name = contentType.getName();
            this.moduleName = contentType.getModuleName();
            this.displayName = contentType.getDisplayName();
            this.isAbstract = contentType.isAbstract();
            this.isFinal = contentType.isFinal();
            this.formItemList = Lists.newArrayList( contentType.form().copy().formItemIterable() );
            this.name = contentType.getName();
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

        public Builder addFormItem( final FormItem formItem )
        {
            this.formItemList.add( formItem );
            return this;
        }

        public Builder formItems( final FormItems formItems )
        {
            for ( FormItem formItem : formItems )
            {
                this.addFormItem( formItem );
            }
            return this;
        }

        public ContentType build()
        {
            final Form.Builder formBuilder = newForm();
            for ( FormItem formItem : formItemList )
            {
                formBuilder.addFormItem( formItem );
            }

            return new ContentType( name, displayName, superType, isAbstract, isFinal, moduleName, formBuilder.build() );
        }
    }
}
