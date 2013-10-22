package com.enonic.wem.admin.json.form;

import com.enonic.wem.api.form.Occurrences;

@SuppressWarnings("UnusedDeclaration")
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
