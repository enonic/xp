package com.enonic.wem.api.schema.content.validator;

import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;

public final class MinimumOccurrencesValidationError
    extends DataValidationError
{
    public MinimumOccurrencesValidationError( final Input input, final int entryCount )
    {
        super( input.getPath(), "Input [{0}] requires minimum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", input.getPath(),
               input.getOccurrences().getMinimum(), entryCount );
    }

    public MinimumOccurrencesValidationError( final FormItemSet formItemSet, final int entryCount )
    {
        super( formItemSet.getPath(), "FormItemSet [{0}] requires minimum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", formItemSet,
               formItemSet.getOccurrences().getMinimum(), entryCount );
    }
}
