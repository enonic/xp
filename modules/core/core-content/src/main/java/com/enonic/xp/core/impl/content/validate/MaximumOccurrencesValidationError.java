package com.enonic.xp.core.impl.content.validate;

import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.Occurrences;

public final class MaximumOccurrencesValidationError
    extends DataValidationError
{
    public MaximumOccurrencesValidationError( final FormItemPath path, final String itemClassName, final Occurrences occurrences,
                                              final int size )
    {
        super( path, itemClassName + " [{0}] allows maximum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", path,
               occurrences.getMaximum(), size );
    }
}
