package com.enonic.wem.core.content.type;


import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.TemplateReferenceFetcher;
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

    public void addConfigItem( final ConfigItem configItem )
    {
        this.configItems.addConfigItem( configItem );
    }

    public Field getField( final String path )
    {
        final ConfigItemPath configItemPath = new ConfigItemPath( path );
        final Field field = configItems.getField( configItemPath );

        Preconditions.checkState( field.getPath().equals( configItemPath ),
                                  "Found Field at path [%s] have unexpected path: " + field.getPath(), configItemPath );
        return field;
    }

    public FieldSet getFieldSet( final String path )
    {
        final ConfigItemPath configItemPath = new ConfigItemPath( path );
        final FieldSet fieldSet = configItems.getFieldSet( configItemPath );
        Preconditions.checkState( fieldSet.getPath().equals( configItemPath ),
                                  "Found FieldSet at path [%s] have unexpected path: " + fieldSet.getPath(), configItemPath );
        return fieldSet;
    }

    public void templateReferencesToConfigItems( final TemplateReferenceFetcher templateReferenceFetcher )
    {
        configItems.templateReferencesToConfigItems( templateReferenceFetcher );
    }
}
