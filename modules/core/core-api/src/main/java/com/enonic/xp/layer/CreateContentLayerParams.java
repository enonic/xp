package com.enonic.xp.layer;

public class CreateContentLayerParams
{
    private String name;

    private String parentName;

    private CreateContentLayerParams( final Builder builder )
    {
        name = builder.name;
        parentName = builder.parentName;
    }

    public String getName()
    {
        return name;
    }

    public String getParentName()
    {
        return parentName;
    }

    public static CreateContentLayerParams from( final String name, final String parentName )
    {
        return create().
            name( name ).
            parentName( parentName ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String name;

        private String parentName;

        private Builder()
        {
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder parentName( final String parentName )
        {
            this.parentName = parentName;
            return this;
        }

        public CreateContentLayerParams build()
        {
            return new CreateContentLayerParams( this );
        }
    }
}

