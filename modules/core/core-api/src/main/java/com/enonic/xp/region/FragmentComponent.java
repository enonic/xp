package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ContentId;

@Beta
public class FragmentComponent
    extends Component
{
    private ContentId fragment;

    public FragmentComponent( final Builder builder )
    {
        super( builder );
        this.fragment = builder.fragment;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final FragmentComponent source )
    {
        return new Builder( source );
    }

    @Override
    public FragmentComponent copy()
    {
        return create( this ).build();
    }

    @Override
    public ComponentType getType()
    {
        return FragmentComponentType.INSTANCE;
    }

    public ContentId getFragment()
    {
        return fragment;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final FragmentComponent that = (FragmentComponent) o;
        return Objects.equals( fragment, that.fragment );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), fragment );
    }

    public static class Builder
        extends Component.Builder
    {
        private ContentId fragment;

        private Builder()
        {
            // Default
        }

        private Builder( final FragmentComponent source )
        {
            super( source );
            fragment = source.fragment;
        }

        public Builder fragment( final ContentId value )
        {
            this.fragment = value;
            return this;
        }

        @Override
        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value != null ? new ComponentName( value ) : null;
            return this;
        }

        public FragmentComponent build()
        {
            return new FragmentComponent( this );
        }
    }
}
