package com.enonic.xp.schema.content.validator;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.inputtype.InputTypeService;
import com.enonic.xp.schema.content.ContentType;


public final class InputValidator
{
    private final Form form;

    private final InputTypeService inputTypeService;

    private InputValidator( final Builder builder )
    {
        this.form = builder.contentType.form();
        this.inputTypeService = builder.inputTypeService;
    }

    public final void validate( final PropertyTree propertyTree )
    {
        if ( this.form != null )
        {
            final InputValidationVisitor inputValidationVisitor = new InputValidationVisitor( propertyTree, this.inputTypeService );
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

        private InputTypeService inputTypeService;

        public Builder contentType( final ContentType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder inputTypeService( final InputTypeService inputTypeService )
        {
            this.inputTypeService = inputTypeService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.contentType, "ContentType is required" );
            Preconditions.checkNotNull( this.inputTypeService, "InputTypeService is required" );
        }

        public InputValidator build()
        {
            validate();
            return new InputValidator( this );
        }
    }
}
