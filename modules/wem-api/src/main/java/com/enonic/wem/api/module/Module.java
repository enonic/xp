package com.enonic.wem.api.module;


import com.google.common.base.Objects;

public class Module
{
    public static final Module SYSTEM = newModule().name( "System" ).build();

    private final String name;

    public Module( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public static ModuleBuilder newModule()
    {
        return new ModuleBuilder();
    }

    public static class ModuleBuilder
    {
        private String name;

        public ModuleBuilder name( String value )
        {
            this.name = value;
            return this;
        }

        public Module build()
        {
            Module module = new Module( name );
            return module;
        }
    }

    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "name", name );
        return s.toString();
    }
}
