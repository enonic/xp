package com.enonic.xp.core.impl.content.validate;

import com.enonic.xp.form.FormOptionSet;

public final class OptionSetSelectionValidationError
    extends DataValidationError
{
    public OptionSetSelectionValidationError( final FormOptionSet optionSet, final int selectionCount )
    {
        super( optionSet.getPath(), "OptionSet [{0}] requires min {1} max {2} items selected: {3}", optionSet.getPath(),
               optionSet.getMultiselection().getMinimum(), optionSet.getMultiselection().getMaximum(), selectionCount );
    }
}
