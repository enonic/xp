package com.enonic.wem.api.content.type.component;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

public class Components
    implements Iterable<Component>
{
    private ComponentPath path;

    private LinkedHashMap<String, Component> componentByName = new LinkedHashMap<String, Component>();

    private LinkedHashMap<String, HierarchicalComponent> hierarchicalComponentByName = new LinkedHashMap<String, HierarchicalComponent>();

    private LinkedHashMap<String, Layout> layoutByName = new LinkedHashMap<String, Layout>();

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

        Object previous = componentByName.put( component.getName(), component );
        Preconditions.checkArgument( previous == null, "Component already added: " + component );

        if ( component instanceof Layout )
        {
            layoutByName.put( component.getName(), (Layout) component );
        }
        else if ( component instanceof HierarchicalComponent )
        {
            hierarchicalComponentByName.put( component.getName(), (HierarchicalComponent) component );
        }
    }

    public void setPath( final ComponentPath path )
    {
        this.path = path;
        for ( final Component component : componentByName.values() )
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
            HierarchicalComponent foundConfig = doGetHierarchicalComponent( firstPathElement );
            if ( foundConfig == null )
            {
                return null;
            }

            if ( foundConfig instanceof ComponentSet )
            {
                ComponentSet componentSet = (ComponentSet) foundConfig;
                return componentSet.getHierarchicalComponent( path.asNewWithoutFirstPathElement() );
            }
            else if ( foundConfig instanceof SubTypeReference )
            {
                throw new IllegalArgumentException(
                    "Cannot get component [" + path + "] because it's past a SubTypeReference [" + foundConfig +
                        "], resolve the SubTypeReference first." );
            }
            else
            {
                return foundConfig;
            }
        }
        else
        {
            return doGetHierarchicalComponent( firstPathElement );
        }
    }

    private HierarchicalComponent doGetHierarchicalComponent( final String name )
    {
        return typeCast( doGetComponent( name ), HierarchicalComponent.class );
    }

    public Component doGetComponent( final String name )
    {
        Preconditions.checkArgument( ComponentPath.hasNotPathElementDivider( name ), "name cannot be a path: %s", name );

        Component foundComponent = componentByName.get( name );
        if ( foundComponent == null )
        {
            foundComponent = searchComponentInLayouts( name );
        }
        return foundComponent;
    }

    public Component getComponent( final String name )
    {
        return doGetComponent( name );
    }

    public HierarchicalComponent getComponent( final ComponentPath path )
    {
        return getHierarchicalComponent( path );
    }

    public Input getInput( final String name )
    {
        return typeCast( doGetComponent( name ), Input.class );
    }

    public Input getInput( final ComponentPath path )
    {
        return typeCast( getHierarchicalComponent( path ), Input.class );
    }

    public ComponentSet getComponentSet( final String name )
    {
        return typeCast( doGetComponent( name ), ComponentSet.class );
    }

    public ComponentSet getComponentSet( final ComponentPath path )
    {
        return typeCast( getHierarchicalComponent( path ), ComponentSet.class );
    }

    public SubTypeReference getSubTypeReference( final String name )
    {
        return typeCast( doGetComponent( name ), SubTypeReference.class );
    }

    public SubTypeReference getSubTypeReference( final ComponentPath path )
    {
        return typeCast( getHierarchicalComponent( path ), SubTypeReference.class );
    }

    public Layout getLayout( final String name )
    {
        return typeCast( doGetComponent( name ), Layout.class );
    }

    public Iterator<Component> iterator()
    {
        return componentByName.values().iterator();
    }

    public Iterable<Component> iterable()
    {
        return componentByName.values();
    }

    public Iterable<HierarchicalComponent> iterableForHierarchicalComponents()
    {
        return hierarchicalComponentByName.values();
    }

    public int size()
    {
        return componentByName.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        int index = 0;
        final int size = componentByName.size();
        for ( Component entry : componentByName.values() )
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
        for ( Component ci : this.componentByName.values() )
        {
            Component copyOfCi = ci.copy();
            copy.componentByName.put( copyOfCi.getName(), copyOfCi );

            if ( copyOfCi instanceof FieldSet )
            {
                copy.layoutByName.put( copyOfCi.getName(), (FieldSet) copyOfCi );
            }
        }
        return copy;
    }

    // TODO: Move method out of here and into it's own class SubTypeResolver?
    public void subTypeReferencesToComponents( final SubTypeFetcher subTypeFetcher )
    {
        for ( final Component component : componentByName.values() )
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
                        final ComponentSet componentSet = (ComponentSet) componentCreatedFromSubType;
                        componentSet.getComponents().subTypeReferencesToComponents( subTypeFetcher );
                    }

                    componentByName.put( component.getName(), componentCreatedFromSubType );
                }
            }
        }
    }

    private Component searchComponentInLayouts( final String name )
    {
        Component foundComponent = null;

        for ( final Layout layout : layoutByName.values() )
        {
            foundComponent = layout.getComponent( name );
            if ( foundComponent != null )
            {
                break;
            }
        }
        return foundComponent;
    }

    private <T extends Component> T typeCast( final Component component, final Class<T> type )
    {
        checkComponentType( type, component );
        //noinspection unchecked
        return (T) component;
    }

    private <T extends Component> void checkComponentType( final Class<T> type, final Component component )
    {
        Preconditions.checkArgument( type.isInstance( component ),
                                     "Component [%s] in [%s] is not of type %s: " + component.getClass().getName(), this.getPath(),
                                     component.getName(), type.getSimpleName() );
    }
}
