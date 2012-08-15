package com.enonic.wem.core.module;


public class Module
{
    private String name;

    void setName( final String name )
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
            Module module = new Module();
            module.setName( name );
            return module;
        }
    }

}
