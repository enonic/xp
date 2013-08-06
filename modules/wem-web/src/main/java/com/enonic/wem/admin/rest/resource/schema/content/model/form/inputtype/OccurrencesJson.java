package com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype;

import com.enonic.wem.admin.rest.resource.model.Item;
import com.enonic.wem.api.schema.content.form.Occurrences;

public class OccurrencesJson
    extends Item
{
    private final Occurrences occurrences;

    public OccurrencesJson( final Occurrences occurrences )
    {
        this.occurrences = occurrences;
    }

    public int getMinimum()
    {
        return this.occurrences.getMinimum();
    }

    public int getMaximum()
    {
        return this.occurrences.getMaximum();
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
