package com.enonic.wem.api.content.type.form;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

public class InputMixin
    extends Mixin
{
    private final Input input;

    InputMixin( final String displayName, final ModuleName moduleName, final Input input )
    {
        super( displayName, moduleName );
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

    public FormItem toFormItem( final MixinReference mixinReference )
    {
        Input input = this.input.copy();
        input.setName( mixinReference.getName() );
        input.setPath( mixinReference.getPath() );
        return input;
    }

    public static Builder newInputMixin()
    {
        return new Builder();
    }

    public static Builder newInputMixin( final InputMixin source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private String displayName;

        private Input input;

        private ModuleName moduleName;

        public Builder()
        {

        }

        public Builder( final InputMixin source )
        {
            this.input = source.input;
            this.moduleName = source.getModuleName();
        }

        public Builder displayName( String value )
        {
            this.displayName = value;
            return this;
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

        public InputMixin build()
        {
            Preconditions.checkNotNull( input, "input is required" );
            Preconditions.checkNotNull( moduleName, "moduleName is required" );
            return new InputMixin( displayName, moduleName, input );
        }
    }
}
