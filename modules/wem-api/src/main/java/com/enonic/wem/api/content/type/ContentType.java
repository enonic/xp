package com.enonic.wem.api.content.type;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.ComponentPath;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.content.type.component.HierarchicalComponent;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.SubTypeFetcher;
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

    private Components components = new Components();

    public ContentType()
    {
        components = new Components();
    }

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

    public void setComponents( final Components components )
    {
        this.components = components;
    }

    public Components getComponents()
    {
        return components;
    }

    public void addComponent( final Component component )
    {
        this.components.add( component );
    }

    Input getInput( final ComponentPath path )
    {
        final Input input = components.getInput( path );
        if ( input == null )
        {
            return null;
        }

        return input;
    }

    public HierarchicalComponent getComponent( final String path )
    {
        return components.getHierarchicalComponent( new ComponentPath( path ) );
    }

    public HierarchicalComponent getComponent( final ComponentPath path )
    {
        return components.getHierarchicalComponent( path );
    }

    public Input getInput( final String path )
    {
        return getInput( new ComponentPath( path ) );
    }

    public ComponentSet getComponentSet( final String path )
    {
        final ComponentPath componentPath = new ComponentPath( path );
        final ComponentSet componentSet = components.getComponentSet( componentPath );
        Preconditions.checkState( componentSet.getPath().equals( componentPath ),
                                  "Found ComponentSet at path [%s] have unexpected path: " + componentSet.getPath(), componentPath );
        return componentSet;
    }

    public void subTypeReferencesToComponents( final SubTypeFetcher subTypeFetcher )
    {
        components.subTypeReferencesToComponents( subTypeFetcher );
    }

}
