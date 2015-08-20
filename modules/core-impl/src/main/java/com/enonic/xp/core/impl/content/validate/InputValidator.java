package com.enonic.xp.core.impl.content.validate;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeResolver;
import com.enonic.xp.schema.content.ContentType;

public final class InputValidator
{
    private final Form form;

    private final InputTypeResolver inputTypeResolver;

    private InputValidator( final Builder builder )
    {
        this.form = builder.contentType.getForm();
        this.inputTypeResolver = builder.inputTypeResolver;
    }

    public final void validate( final PropertyTree propertyTree )
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
        private ContentType contentType;

        private InputTypeResolver inputTypeResolver;

        public Builder contentType( final ContentType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder inputTypeResolver( final InputTypeResolver inputTypeResolver )
        {
            this.inputTypeResolver = inputTypeResolver;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.contentType, "ContentType is required" );
            Preconditions.checkNotNull( this.inputTypeResolver, "InputTypeResolver is required" );
        }

        public InputValidator build()
        {
            validate();
            return new InputValidator( this );
        }
    }
}
