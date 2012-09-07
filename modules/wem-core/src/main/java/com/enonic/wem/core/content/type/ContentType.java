package com.enonic.wem.core.content.type;


import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.content.type.formitem.Component;
import com.enonic.wem.core.content.type.formitem.FormItem;
import com.enonic.wem.core.content.type.formitem.FormItemPath;
import com.enonic.wem.core.content.type.formitem.FormItemSet;
import com.enonic.wem.core.content.type.formitem.FormItems;
import com.enonic.wem.core.content.type.formitem.TemplateFetcher;
import com.enonic.wem.core.module.Module;

public class ContentType
{
    private String name;

    private ContentType superType;

    private ContentHandler contentHandler;

    private boolean isAbstract;

    private Module module;

    private ComputedDisplayName computedDisplayName;

    private FormItems formItems = new FormItems();

    public ContentType()
    {
        formItems = new FormItems();
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public ContentTypeQualifiedName getQualifiedName()
    {
        return new ContentTypeQualifiedName( module.getName(), name );
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

    public void setFormItems( final FormItems formItems )
    {
        this.formItems = formItems;
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.addFormItem( formItem );
    }

    Component getComponent( final FormItemPath path )
    {
        final Component component = formItems.getComponent( path );
        if ( component == null )
        {
            return null;
        }

        Preconditions.checkState( component.getPath().equals( path ),
                                  "Found Component at path [%s] have unexpected path: " + component.getPath(), path );
        return component;
    }

    public Component getComponent( final String path )
    {
        return getComponent( new FormItemPath( path ) );
    }

    public FormItemSet getFormItemSet( final String path )
    {
        final FormItemPath formItemPath = new FormItemPath( path );
        final FormItemSet formItemSet = formItems.getFormItemSet( formItemPath );
        Preconditions.checkState( formItemSet.getPath().equals( formItemPath ),
                                  "Found FormItemSet at path [%s] have unexpected path: " + formItemSet.getPath(), formItemPath );
        return formItemSet;
    }

    public void templateReferencesToFormItems( final TemplateFetcher templateFetcher )
    {
        formItems.templateReferencesToFormItems( templateFetcher );
    }

}
