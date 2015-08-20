package com.enonic.xp.core.impl.content.validate;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.inputtype.InputType;
import com.enonic.xp.inputtype.InputTypeResolver;

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
    {
        if ( property == null )
        {
            return;
        }

        final InputType type = this.inputTypeResolver.resolve( input.getInputType() );
        type.validate( property, input.getInputTypeConfig() );
    }
}
