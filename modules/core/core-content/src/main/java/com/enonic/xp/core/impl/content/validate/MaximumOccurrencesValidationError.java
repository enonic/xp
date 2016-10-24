package com.enonic.xp.core.impl.content.validate;

import com.enonic.xp.form.GenericFormItem;

public final class MaximumOccurrencesValidationError
    extends DataValidationError
{
    public MaximumOccurrencesValidationError( final GenericFormItem set, final int size )
    {
        super( set.getPath(), set.getClass().getSimpleName() + " [{0}] allows maximum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}",
               set.getPath(),
               set.getOccurrences().getMaximum(), size );
    }
}
