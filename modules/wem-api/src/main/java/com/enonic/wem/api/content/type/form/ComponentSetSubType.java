package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.Module;

public class ComponentSetSubType
    extends SubType
{
    private ComponentSet componentSet = new ComponentSet();

    ComponentSetSubType( final Module module )
    {
        super( module );
    }

    public String getName()
    {
        return componentSet.getName();
    }

    @Override
    public Class getType()
    {
        return this.getClass();
    }

    public void addComponent( final HierarchicalComponent component )
    {
        if ( component instanceof SubTypeReference )
        {
            final SubTypeReference subTypeReference = (SubTypeReference) component;
            Preconditions.checkArgument( subTypeReference.getSubTypeClass().equals( InputSubType.class ),
                                         "A SubType cannot reference other SubTypes unless it is of type %s: " +
                                             subTypeReference.getSubTypeClass().getSimpleName(), InputSubType.class.getSimpleName() );
        }
        componentSet.add( component );
    }

    public HierarchicalComponent create( final SubTypeReference subTypeReference )
    {
        final ComponentSet newComponentSet = this.componentSet.copy();
        newComponentSet.setName( subTypeReference.getName() );
        newComponentSet.setPath( subTypeReference.getPath() );
        return newComponentSet;
    }

    public static Builder newComponentSetSubType()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Module module;

        private ComponentSet componentSet;

        public Builder module( Module value )
        {
            this.module = value;
            return this;
        }

        public Builder componentSet( ComponentSet value )
        {
            this.componentSet = value;
            return this;
        }

        public ComponentSetSubType build()
        {
            ComponentSetSubType subType = new ComponentSetSubType( module );
            subType.componentSet = componentSet;
            return subType;
        }
    }
}
