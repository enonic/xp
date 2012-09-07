package com.enonic.wem.core.content.type;


import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.Component;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FormItem;
import com.enonic.wem.core.content.type.configitem.FormItemSet;
import com.enonic.wem.core.content.type.configitem.TemplateFetcher;
import com.enonic.wem.core.module.Module;

public class ContentType
{
    private String name;

    private ContentType superType;

    private ContentHandler contentHandler;

    private boolean isAbstract;

    private Module module;

    private ComputedDisplayName computedDisplayName;

    private ConfigItems configItems = new ConfigItems();

    public ContentType()
    {
        configItems = new ConfigItems();
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

    public void setConfigItems( final ConfigItems configItems )
    {
        this.configItems = configItems;
    }

    public ConfigItems getConfigItems()
    {
        return configItems;
    }

    public void addConfigItem( final FormItem formItem )
    {
        this.configItems.addConfigItem( formItem );
    }

    Component getField( final ConfigItemPath path )
    {
        final Component component = configItems.getField( path );
        if ( component == null )
        {
            return null;
        }

        Preconditions.checkState( component.getPath().equals( path ),
                                  "Found Field at path [%s] have unexpected path: " + component.getPath(), path );
        return component;
    }

    public Component getField( final String path )
    {
        return getField( new ConfigItemPath( path ) );
    }

    public FormItemSet getFieldSet( final String path )
    {
        final ConfigItemPath configItemPath = new ConfigItemPath( path );
        final FormItemSet formItemSet = configItems.getFieldSet( configItemPath );
        Preconditions.checkState( formItemSet.getPath().equals( configItemPath ),
                                  "Found FieldSet at path [%s] have unexpected path: " + formItemSet.getPath(), configItemPath );
        return formItemSet;
    }

    public void templateReferencesToConfigItems( final TemplateFetcher templateFetcher )
    {
        configItems.templateReferencesToConfigItems( templateFetcher );
    }

}
