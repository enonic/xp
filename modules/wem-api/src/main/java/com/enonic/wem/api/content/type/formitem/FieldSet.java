package com.enonic.wem.api.content.type.formitem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;


public class FieldSet
    extends Layout
    implements Iterable<Component>
{
    private String label;

    private Components components = new Components();

    protected FieldSet()
    {
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public Iterator<Component> iterator()
    {
        return components.iterator();
    }

    @Override
    public FieldSet copy()
    {
        final FieldSet copy = (FieldSet) super.copy();
        copy.label = label;
        copy.components = components.copy();
        return copy;
    }

    public static Builder newFieldSet()
    {
        return new Builder();
    }

    public void addComponent( final Component component )
    {
        this.components.add( component );
    }

    public Components getComponents()
    {
        return components;
    }

    public Component getComponent( final String name )
    {
        return components.getComponent( name );
    }

    void forwardSetPath( ComponentPath path )
    {
        components.setPath( path );
    }

    public Iterable<Component> getComponentsIterable()
    {
        return components.iterable();
    }

    public static class Builder
    {
        private String label;

        private String name;

        private List<Component> components = new ArrayList<Component>();

        public Builder label( String value )
        {
            this.label = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder add( Component component )
        {
            this.components.add( component );
            return this;
        }

        public FieldSet build()
        {
            Preconditions.checkNotNull( this.label, "label is required" );
            Preconditions.checkNotNull( this.name, "name is required" );

            FieldSet fieldSet = new FieldSet();
            fieldSet.label = this.label;
            fieldSet.setName( this.name );
            for ( Component component : components )
            {
                fieldSet.addComponent( component );
            }
            return fieldSet;
        }
    }
}
