package com.enonic.xp.core.impl.content.validate;

import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeResolver;

public final class InputValidator
{
    private final Form form;

    private final InputTypeResolver inputTypeResolver;

    private InputValidator( final Builder builder )
    {
        this.form = builder.form;
        this.inputTypeResolver = builder.inputTypeResolver;
    }

    public void validate( final PropertyTree propertyTree )
    {
        if ( this.form != null )
        {
            final InputValidationVisitor inputValidationVisitor = new InputValidationVisitor( propertyTree, this.inputTypeResolver );
            inputValidationVisitor.traverse( this.form );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Form form;

        private InputTypeResolver inputTypeResolver;

        public Builder form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder inputTypeResolver( final InputTypeResolver inputTypeResolver )
        {
            this.inputTypeResolver = inputTypeResolver;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( this.form );
            Objects.requireNonNull( this.inputTypeResolver );
        }

        public InputValidator build()
        {
            validate();
            return new InputValidator( this );
        }
    }
}
