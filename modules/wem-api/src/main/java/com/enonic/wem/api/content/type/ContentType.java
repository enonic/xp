package com.enonic.wem.api.content.type;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.type.form.Form;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.module.Module;

public class ContentType
{
    private String name;

    private String displayName;

    private ContentType superType;

    private ContentHandler contentHandler;

    private boolean isAbstract;

    private Module module;

    private ComputedDisplayName computedDisplayName;

    private final Form form = new Form();

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public QualifiedContentTypeName getQualifiedName()
    {
        return new QualifiedContentTypeName( module.getName(), name );
    }

    public ContentType getSuperType()
    {
        return superType;
    }

    public void setSuperType( final ContentType superType )
    {
        this.superType = superType;
    }

    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    public void setContentHandler( final ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public boolean isAbstract()
    {
        return isAbstract;
    }

    public void setAbstract( final boolean anAbstract )
    {
        isAbstract = anAbstract;
    }

    public Module getModule()
    {
        return module;
    }

    public void setModule( final Module module )
    {
        this.module = module;
    }

    public ComputedDisplayName getComputedDisplayName()
    {
        return computedDisplayName;
    }

    public void setComputedDisplayName( final ComputedDisplayName computedDisplayName )
    {
        this.computedDisplayName = computedDisplayName;
    }

    public Form form()
    {
        return this.form;
    }

    public static Builder newContentType()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private Module module;

        private String displayName;

        private boolean isAbstract;


        private List<FormItem> formItemList = new ArrayList<FormItem>();

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        public Builder module( Module module )
        {
            this.module = module;
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

        public Builder add( FormItem formItem )
        {
            this.formItemList.add( formItem );
            return this;
        }

        public Builder formItems( final FormItems formItems )
        {
            for ( FormItem formItem : formItems )
            {
                this.add( formItem );
            }
            return this;
        }

        public ContentType build()
        {
            ContentType type = new ContentType();
            type.setName( name );
            type.setModule( module );
            type.setDisplayName( displayName );
            type.setAbstract( isAbstract );

            for ( FormItem formItem : formItemList )
            {
                type.form.addFormItem( formItem );
            }
            return type;
        }
    }

}
