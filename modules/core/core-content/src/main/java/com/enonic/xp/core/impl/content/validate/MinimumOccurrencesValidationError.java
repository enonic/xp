package com.enonic.xp.core.impl.content.validate;

import com.enonic.xp.form.GenericFormItem;

public final class MinimumOccurrencesValidationError
    extends DataValidationError
{
    public MinimumOccurrencesValidationError( final GenericFormItem genericFormItem, final int entryCount )
    {
        super( genericFormItem.getPath(),
               genericFormItem.getClass().getSimpleName() + " [{0}] requires minimum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}",
               genericFormItem.getPath(), genericFormItem.getOccurrences().getMinimum(), entryCount );
    }
}
