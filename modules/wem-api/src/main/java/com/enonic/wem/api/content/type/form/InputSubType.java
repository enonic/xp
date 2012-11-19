package com.enonic.wem.api.content.type.form;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.Module;

public class InputSubType
    extends SubType
{
    private Input input;

    InputSubType( final Module module )
    {
        super( module );
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

    public HierarchicalFormItem create( final SubTypeReference subTypeReference )
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

    public static class Builder
    {
        private Input input;

        private Module module;

        public Builder input( Input value )
        {
            this.input = value;
            return this;
        }

        public Builder module( Module value )
        {
            this.module = value;
            return this;
        }

        public InputSubType build()
        {
            Preconditions.checkNotNull( input, "input is required" );

            InputSubType subType = new InputSubType( module );
            subType.input = input;
            return subType;
        }
    }
}
