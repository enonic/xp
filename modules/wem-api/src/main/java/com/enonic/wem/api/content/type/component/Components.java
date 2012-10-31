package com.enonic.wem.api.content.type.component;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

public class Components
{
    private ComponentPath path;

    private LinkedHashMap<String, Component> items = new LinkedHashMap<String, Component>();

    private LinkedHashMap<String, HierarchicalComponent> hierarchicalComponents = new LinkedHashMap<String, HierarchicalComponent>();

    private LinkedHashMap<String, Layout> layouts = new LinkedHashMap<String, Layout>();

    public Components()
    {
        path = new ComponentPath();
    }

    public ComponentPath getPath()
    {
        return path;
    }

    public void add( final Component component )
    {
        if ( component instanceof HierarchicalComponent )
        {
            ( (HierarchicalComponent) component ).setPath( new ComponentPath( path, component.getName() ) );
        }

        Object previous = items.put( component.getName(), component );
        Preconditions.checkArgument( previous == null, "Component already added: " + component );

        if ( component instanceof Layout )
        {
            layouts.put( component.getName(), (Layout) component );
        }
        else if ( component instanceof HierarchicalComponent )
        {
            hierarchicalComponents.put( component.getName(), (HierarchicalComponent) component );
        }
    }

    public void setPath( final ComponentPath path )
    {
        this.path = path;
        for ( final Component component : items.values() )
        {
            if ( component instanceof HierarchicalComponent )
            {
                ( (HierarchicalComponent) component ).setParentPath( path );
            }
            else if ( component instanceof FieldSet )
            {
                ( (FieldSet) component ).forwardSetPath( path );
            }
        }
    }

    public HierarchicalComponent getHierarchicalComponent( final ComponentPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        final String firstPathElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            HierarchicalComponent foundConfig = getHierarchicalComponent( firstPathElement );
            if ( foundConfig == null )
            {
                return null;
            }

            if ( foundConfig instanceof ComponentSet )
            {
                ComponentSet componentSet = (ComponentSet) foundConfig;
                return componentSet.getHierarchicalComponent( path.asNewWithoutFirstPathElement() );
            }
            else
            {
                return foundConfig;
            }
        }
        else
        {
            return getHierarchicalComponent( firstPathElement );
        }
    }

    public Component getComponent( final String name )
    {
        Component foundComponent = items.get( name );
        if ( foundComponent == null )
        {
            foundComponent = searchComponentInLayouts( name );
        }
        return foundComponent;
    }

    public HierarchicalComponent getHierarchicalComponent( final String name )
    {
        Component component = getComponent( name );
        if ( component == null )
        {
            return null;
        }

        Preconditions.checkArgument( component instanceof HierarchicalComponent,
                                     "Component [%s] in [%s] is not of type HierarchicalComponent: " + component.getClass().getName(),
                                     this.getPath(), component.getName() );

        //noinspection ConstantConditions
        return (HierarchicalComponent) component;
    }

    public ComponentSet getComponentSet( final ComponentPath path )
    {
        final HierarchicalComponent component = getHierarchicalComponent( path );
        if ( component == null )
        {
            return null;
        }

        Preconditions.checkArgument( ( component instanceof ComponentSet ),
                                     "Component at path [%s] is not a ComponentSet: " + component.getClass().getSimpleName(),
                                     component.getPath() );

        //noinspection ConstantConditions
        return (ComponentSet) component;
    }

    public Input getInput( final ComponentPath path )
    {
        final HierarchicalComponent component = getHierarchicalComponent( path );
        if ( component == null )
        {
            return null;
        }
        Preconditions.checkArgument( component instanceof Input,
                                     "Component at path [%s] is not a Input: " + component.getClass().getSimpleName(),
                                     component.getPath() );

        //noinspection ConstantConditions
        return (Input) component;
    }

    public Iterator<Component> iterator()
    {
        return items.values().iterator();
    }

    public Iterable<Component> iterable()
    {
        return items.values();
    }

    public Iterable<HierarchicalComponent> iterableForHierarchicalComponents()
    {
        return hierarchicalComponents.values();
    }

    public int size()
    {
        return items.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        int index = 0;
        final int size = items.size();
        for ( Component entry : items.values() )
        {
            if ( entry instanceof HierarchicalComponent )
            {
                s.append( entry.getName() );
                if ( index < size - 1 )
                {
                    s.append( ", " );
                }
                index++;
            }
            else if ( entry instanceof FieldSet )
            {
                FieldSet fieldSet = (FieldSet) entry;
                s.append( fieldSet.getName() ).append( "{" );
                s.append( fieldSet.getComponents().toString() );
                s.append( "}" );
            }
        }
        return s.toString();
    }

    public Components copy()
    {
        Components copy = new Components();
        copy.path = path;
        for ( Component ci : this.items.values() )
        {
            Component copyOfCi = ci.copy();
            copy.items.put( copyOfCi.getName(), copyOfCi );

            if ( copyOfCi instanceof FieldSet )
            {
                copy.layouts.put( copyOfCi.getName(), (FieldSet) copyOfCi );
            }
        }
        return copy;
    }

    public void subTypeReferencesToComponents( final SubTypeFetcher subTypeFetcher )
    {
        for ( final Component component : items.values() )
        {
            if ( component instanceof SubTypeReference )
            {
                final SubTypeReference subTypeReference = (SubTypeReference) component;
                final SubType subType = subTypeFetcher.getSubType( subTypeReference.getSubTypeQualifiedName() );
                if ( subType != null )
                {
                    Preconditions.checkArgument( subTypeReference.getSubTypeClass() == subType.getType(),
                                                 "SubType expected to be of type %s: " + subType.getType().getSimpleName(),
                                                 subTypeReference.getSubTypeClass().getSimpleName() );

                    final HierarchicalComponent componentCreatedFromSubType = subType.create( subTypeReference );
                    if ( componentCreatedFromSubType instanceof ComponentSet )
                    {
                        ComponentSet componentSet = (ComponentSet) componentCreatedFromSubType;
                        componentSet.getComponents().subTypeReferencesToComponents( subTypeFetcher );
                    }

                    items.put( component.getName(), componentCreatedFromSubType );
                }
            }
        }
    }

    private Component searchComponentInLayouts( final String name )
    {
        Component foundComponent = null;

        for ( final Layout layout : layouts.values() )
        {
            foundComponent = layout.getComponent( name );
            if ( foundComponent != null )
            {
                break;
            }
        }
        return foundComponent;
    }

    public Iterable<Component> getIterable()
    {
        return items.values();
    }
}
