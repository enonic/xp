package com.enonic.xp.schema.content.validator;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.schema.content.ContentType;


public final class InputValidator
{
    private final Form form;

    private InputValidator( final ContentType contentType )
    {
        form = contentType.form();
    }

    public final void validate( final PropertyTree propertyTree )
    {
        if ( form != null )
        {
            final InputValidationVisitor inputValidationVisitor = new InputValidationVisitor( propertyTree );
            inputValidationVisitor.traverse( form );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentType contentType;

        public Builder contentType( final ContentType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentType, "No content type given" );
        }

        public InputValidator build()
        {
            this.validate();
            return new InputValidator( this.contentType );
        }
    }

    private class InputValidationVisitor
        extends InputVisitor
    {
        private PropertyTree propertyTree;

        public InputValidationVisitor( final PropertyTree propertyTree )
        {
            this.propertyTree = propertyTree;
        }

        @Override
        public void visit( final Input input )
        {

            final Property property = propertyTree.getProperty( input.getPath().toString() );

            if ( property != null )
            {
                input.checkValidity( property );
            }
        }
    }
}

