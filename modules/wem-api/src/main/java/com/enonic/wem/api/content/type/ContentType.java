package com.enonic.wem.api.content.type;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItemPath;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.content.type.form.HierarchicalFormItem;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.SubTypeFetcher;
import com.enonic.wem.api.content.type.form.SubTypeReference;
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

    private final FormItems formItems = new FormItems();

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

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.add( formItem );
    }

    public Iterable<FormItem> formItemIterable()
    {
        return formItems;
    }

    public HierarchicalFormItem getFormItem( final String path )
    {
        return formItems.getFormItem( new FormItemPath( path ) );
    }

    public HierarchicalFormItem getFormItem( final FormItemPath path )
    {
        return formItems.getFormItem( path );
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    public Input getInput( final FormItemPath path )
    {
        return formItems.getInput( path );
    }

    public Input getInput( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path ) ? formItems.getInput( path ) : formItems.getInput( new FormItemPath( path ) );
    }

    public FormItemSet getFormItemSet( final FormItemPath path )
    {
        return formItems.getFormItemSet( path );
    }

    public FormItemSet getFormItemSet( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path )
            ? formItems.getFormItemSet( path )
            : formItems.getFormItemSet( new FormItemPath( path ) );
    }

    public SubTypeReference getSubTypeReference( final FormItemPath path )
    {
        return formItems.getSubTypeReference( path );
    }

    public SubTypeReference getSubTypeReference( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path )
            ? formItems.getSubTypeReference( path )
            : formItems.getSubTypeReference( new FormItemPath( path ) );
    }

    public void subTypeReferencesToFormItems( final SubTypeFetcher subTypeFetcher )
    {
        formItems.subTypeReferencesToFormItems( subTypeFetcher );
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
                type.addFormItem( formItem );
            }
            return type;
        }
    }

}
