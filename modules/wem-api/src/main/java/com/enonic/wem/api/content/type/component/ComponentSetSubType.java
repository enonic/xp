package com.enonic.wem.api.content.type.component;


import com.google.common.base.Preconditions;

public class ComponentSetSubType
    extends SubType
{
    private ComponentSet componentSet = new ComponentSet();

    ComponentSetSubType()
    {
    }

    public String getName()
    {
        return componentSet.getName();
    }

    void setComponentSet( final ComponentSet componentSet )
    {
        this.componentSet = componentSet;
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
}
