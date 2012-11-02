package com.enonic.wem.api.content.type.component;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class ComponentSet
    extends HierarchicalComponent
{
    private String label;

    private Components components = new Components();

    private boolean immutable;

    private final Occurrences occurrences = new Occurrences( 0, 1 );

    private String customText;

    private String helpText;

    protected ComponentSet()
    {
    }

    @Override
    void setPath( final ComponentPath componentPath )
    {
        super.setPath( componentPath );
        components.setPath( componentPath );
    }

    public void add( final Component component )
    {
        if ( component instanceof HierarchicalComponent )
        {
            Preconditions.checkState( getPath() != null, "Cannot add HierarchicalComponent before this ComponentSet have a path" );
            final HierarchicalComponent hierarchicalComponent = (HierarchicalComponent) component;
            hierarchicalComponent.setPath( new ComponentPath( getPath(), component.getName() ) );
        }

        this.components.add( component );
    }

    public String getLabel()
    {
        return label;
    }

    public boolean isRequired()
    {
        return occurrences.impliesRequired();
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    public boolean isMultiple()
    {
        return occurrences.isMultiple();
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public String getCustomText()
    {
        return customText;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public Components getComponents()
    {
        return components;
    }

    @Override
    void setParentPath( final ComponentPath parentPath )
    {
        super.setParentPath( parentPath );
        for ( HierarchicalComponent component : components.iterableForHierarchicalComponents() )
        {
            component.setParentPath( this.getPath() );
        }
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        ComponentPath componentPath = getPath();
        if ( componentPath != null )
        {
            s.append( componentPath.toString() );
        }
        else
        {
            s.append( getName() ).append( "?" );
        }
        if ( isMultiple() )
        {
            s.append( "[]" );
        }

        return s.toString();
    }

    @Override
    public ComponentSet copy()
    {
        ComponentSet copy = (ComponentSet) super.copy();
        copy.label = label;
        copy.immutable = immutable;
        copy.occurrences.setMinOccurences( occurrences.getMinimum() );
        copy.occurrences.setMaxOccurences( occurrences.getMaximum() );
        copy.customText = customText;
        copy.helpText = helpText;
        copy.components = components.copy();
        return copy;
    }

    public static Builder newComponentSet()
    {
        return new Builder();
    }

    HierarchicalComponent getHierarchicalComponent( final ComponentPath componentPath )
    {
        return components.getHierarchicalComponent( componentPath );
    }

    public Input getInput( final String name )
    {
        return components.getInput( name );
    }

    public Input getInput( final ComponentPath componentPath )
    {
        return components.getInput( componentPath );
    }

    public ComponentSet getComponentSet( final String name )
    {
        return components.getComponentSet( name );
    }

    public ComponentSet getComponentSet( final ComponentPath componentPath )
    {
        return components.getComponentSet( componentPath );
    }

    public SubTypeReference getSubTypeReference( final ComponentPath componentPath )
    {
        return components.getSubTypeReference( componentPath );
    }

    public SubTypeReference getSubTypeReference( final String name )
    {
        return components.getSubTypeReference( name );
    }

    public Layout getLayout( final String name )
    {
        return components.getLayout( name );
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean immutable;

        private Occurrences occurrences = new Occurrences( 0, 1 );

        private String customText;

        private String helpText;

        private List<Component> components = new ArrayList<Component>();

        private Builder()
        {

        }

        public Builder name( String value )
        {
            name = value;
            return this;
        }

        public Builder label( String value )
        {
            label = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            immutable = value;
            return this;
        }

        public Builder occurrences( Occurrences value )
        {
            occurrences = value;
            return this;
        }

        public Builder occurrences( int minOccurrences, int maxOccurrences )
        {
            occurrences = new Occurrences( minOccurrences, maxOccurrences );
            return this;
        }

        public Builder required( boolean value )
        {
            if ( value && !occurrences.impliesRequired() )
            {
                occurrences.setMinOccurences( 1 );
            }
            else if ( !value && occurrences.impliesRequired() )
            {
                occurrences.setMinOccurences( 0 );
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                occurrences.setMaxOccurences( 0 );
            }
            else
            {
                occurrences.setMaxOccurences( 1 );
            }
            return this;
        }

        public Builder customText( String value )
        {
            customText = value;
            return this;
        }

        public Builder helpText( String value )
        {
            helpText = value;
            return this;
        }

        public Builder add( Component value )
        {
            components.add( value );
            return this;
        }

        public ComponentSet build()
        {
            ComponentSet componentSet = new ComponentSet();
            componentSet.setName( name );
            componentSet.label = label;
            componentSet.immutable = immutable;
            componentSet.occurrences.setMinOccurences( occurrences.getMinimum() );
            componentSet.occurrences.setMaxOccurences( occurrences.getMaximum() );
            componentSet.customText = customText;
            componentSet.helpText = helpText;

            Preconditions.checkNotNull( componentSet.getName(), "a name for the ComponentSet is required" );
            componentSet.setPath( new ComponentPath( componentSet.getName() ) );

            for ( Component component : components )
            {
                componentSet.add( component );
            }
            return componentSet;
        }
    }
}
