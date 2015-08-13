package com.enonic.xp.schema.content.validator;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.form.InvalidDataException;
import com.enonic.xp.form.inputtype.InputType;
import com.enonic.xp.form.inputtype.InputTypeResolver;

final class InputValidationVisitor
    extends InputVisitor
{
    private final PropertyTree propertyTree;

    private final InputTypeResolver inputTypeResolver;

    public InputValidationVisitor( final PropertyTree propertyTree, final InputTypeResolver inputTypeResolver )
    {
        this.propertyTree = propertyTree;
        this.inputTypeResolver = inputTypeResolver;
    }

    @Override
    public void visit( final Input input )
    {
        final Property property = propertyTree.getProperty( input.getPath().toString() );
        if ( property != null )
        {
            checkValidity( input, property );
        }
    }

    private void checkValidity( final Input input, final Property property )
        throws InvalidDataException
    {
        try
        {
            if ( property == null )
            {
                return;
            }

            final InputType type = this.inputTypeResolver.resolve( input.getInputType() );
            type.checkValidity( input.getInputTypeConfig(), property );
        }
        catch ( final Exception e )
        {
            throw new InvalidDataException( property, e );
        }
    }
}
