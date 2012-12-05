package com.enonic.wem.api.content.type.form;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

public class InputSubType
    extends SubType
{
    private final Input input;

    InputSubType( final ModuleName moduleName, final Input input )
    {
        super( moduleName );
        Preconditions.checkNotNull( input, "input is required" );
        this.input = input;
    }

    public String getName()
    {
        return input.getName();
    }

    @Override
    public Class getType()
    {
        return this.getClass();
    }

    public Input getInput()
    {
        return input;
    }

    public FormItem create( final SubTypeReference subTypeReference )
    {
        Input input = this.input.copy();
        input.setName( subTypeReference.getName() );
        input.setPath( subTypeReference.getPath() );
        return input;
    }

    public static Builder newInputSubType()
    {
        return new Builder();
    }

    public static Builder newInputSubType( final InputSubType source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private Input input;

        private ModuleName moduleName;

        public Builder()
        {

        }

        public Builder( final InputSubType source )
        {
            this.input = source.input;
            this.moduleName = source.getModuleName();
        }

        public Builder input( Input value )
        {
            this.input = value;
            return this;
        }

        public Builder module( ModuleName value )
        {
            this.moduleName = value;
            return this;
        }

        public InputSubType build()
        {
            Preconditions.checkNotNull( input, "input is required" );
            Preconditions.checkNotNull( moduleName, "moduleName is required" );
            return new InputSubType( moduleName, input );
        }
    }
}
