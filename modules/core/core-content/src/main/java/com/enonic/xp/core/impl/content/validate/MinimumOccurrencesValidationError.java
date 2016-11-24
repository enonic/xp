package com.enonic.xp.core.impl.content.validate;

import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.Occurrences;

public final class MinimumOccurrencesValidationError
    extends DataValidationError
{
    public MinimumOccurrencesValidationError( final FormItemPath path, final String itemClassName, final Occurrences occurrences,
                                              final int size )
    {
        super( path, itemClassName + " [{0}] requires minimum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", path,
               occurrences.getMinimum(), size );
    }
}
