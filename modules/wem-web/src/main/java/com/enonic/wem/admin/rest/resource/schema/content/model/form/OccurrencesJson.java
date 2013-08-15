package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import com.enonic.wem.api.schema.content.form.Occurrences;

public class OccurrencesJson
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
}
