package com.enonic.xp.region;


import com.google.common.annotations.Beta;

@Beta
public final class PartComponent
    extends DescriptorBasedComponent
{
    private static final ComponentName NAME = ComponentName.from( "Part" );

    public PartComponent( final Builder builder )
    {
        super( builder );
    }

    public static <T extends Builder<T>> Builder<T> create()
    {
        return new Builder();
    }

    public static <T extends Builder<T>> Builder<T> create( final PartComponent source )
    {
        return new Builder( source );
    }

    @Override
    public Component copy()
    {
        return create( this ).build();
    }

    @Override
    public ComponentType getType()
    {
        return PartComponentType.INSTANCE;
    }

    @Override
    public ComponentName getName()
    {
        return NAME;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PartComponent ) )
        {
            return false;
        }

        return super.equals( o );
    }

    public static class Builder<T extends Builder<T>>
        extends DescriptorBasedComponent.Builder<T>
    {
        private Builder()
        {
        }

        private Builder( final PartComponent source )
        {
            super( source );
        }

        public PartComponent build()
        {
            return new PartComponent( this );
        }
    }
}
