package com.enonic.wem.api.module;


import com.google.common.base.Objects;

public class Module
{
    public static final Module SYSTEM = newModule().name( ModuleName.SYSTEM ).build();

    private final ModuleName name;

    public Module( final ModuleName name )
    {
        this.name = name;
    }

    public ModuleName getName()
    {
        return name;
    }

    public static ModuleBuilder newModule()
    {
        return new ModuleBuilder();
    }

    public static class ModuleBuilder
    {
        private ModuleName name;

        public ModuleBuilder name( ModuleName value )
        {
            this.name = value;
            return this;
        }

        public ModuleBuilder name( String value )
        {
            this.name = new ModuleName( value );
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
