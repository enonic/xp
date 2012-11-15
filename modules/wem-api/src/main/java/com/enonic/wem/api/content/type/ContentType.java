package com.enonic.wem.api.content.type;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.ComponentPath;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.content.type.component.HierarchicalComponent;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.SubTypeFetcher;
import com.enonic.wem.api.content.type.component.SubTypeReference;
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

    private final Components components = new Components();

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

    public void addComponent( final Component component )
    {
        this.components.add( component );
    }

    public Iterable<Component> componentIterable()
    {
        return components;
    }

    public HierarchicalComponent getComponent( final String path )
    {
        return components.getComponent( new ComponentPath( path ) );
    }

    public HierarchicalComponent getComponent( final ComponentPath path )
    {
        return components.getComponent( path );
    }

    public Components getComponents()
    {
        return components;
    }

    public Input getInput( final ComponentPath path )
    {
        return components.getInput( path );
    }

    public Input getInput( final String path )
    {
        return ComponentPath.hasNotPathElementDivider( path )
            ? components.getInput( path )
            : components.getInput( new ComponentPath( path ) );
    }

    public ComponentSet getComponentSet( final ComponentPath path )
    {
        return components.getComponentSet( path );
    }

    public ComponentSet getComponentSet( final String path )
    {
        return ComponentPath.hasNotPathElementDivider( path )
            ? components.getComponentSet( path )
            : components.getComponentSet( new ComponentPath( path ) );
    }

    public SubTypeReference getSubTypeReference( final ComponentPath path )
    {
        return components.getSubTypeReference( path );
    }

    public SubTypeReference getSubTypeReference( final String path )
    {
        return ComponentPath.hasNotPathElementDivider( path )
            ? components.getSubTypeReference( path )
            : components.getSubTypeReference( new ComponentPath( path ) );
    }

    public void subTypeReferencesToComponents( final SubTypeFetcher subTypeFetcher )
    {
        components.subTypeReferencesToComponents( subTypeFetcher );
    }

    public static Builder newComponentType()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private Module module;

        private String displayName;

        private boolean isAbstract;


        private List<Component> componentList = new ArrayList<Component>();

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

        public Builder add( Component component )
        {
            this.componentList.add( component );
            return this;
        }

        public Builder components( final Components components )
        {
            for ( Component component : components )
            {
                this.add( component );
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

            for ( Component component : componentList )
            {
                type.addComponent( component );
            }
            return type;
        }
    }

}
