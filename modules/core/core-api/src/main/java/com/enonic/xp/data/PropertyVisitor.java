package com.enonic.xp.data;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class PropertyVisitor
{
    private ValueType valueType;

    private boolean visitPropertiesWithSet = false;

    public PropertyVisitor restrictType( final ValueType valueType )
    {
        this.valueType = valueType;
        return this;
    }

    public PropertyVisitor visitPropertiesWithSet( final boolean value )
    {
        visitPropertiesWithSet = value;
        return this;
    }

    public void traverse( final PropertyTree propertyTree )
    {
        traverse( propertyTree.getRoot() );
    }

    public void traverse( final PropertySet propertySet )
    {
        for ( final Property property : propertySet.getProperties() )
        {
            final boolean valueTypeRestrictionSatisfied = valueType == null || valueType.equals( property.getValue().getType() );
            final boolean propertyWithSetRestrictionSatisfied = visitPropertiesWithSet || ( property.getType() != ValueTypes.PROPERTY_SET );
            if ( valueTypeRestrictionSatisfied && propertyWithSetRestrictionSatisfied )
            {
                visit( property );
            }

            if ( property.getType() == ValueTypes.PROPERTY_SET )
            {
                if ( property.hasNotNullValue() )
                {
                    traverse( property.getSet() );
                }
            }
        }
    }

    public abstract void visit( Property property );
}
