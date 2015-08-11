package com.enonic.xp.admin.impl.json.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;

import com.enonic.xp.form.Occurrences;

@Beta
@SuppressWarnings("UnusedDeclaration")
public class OccurrencesJson
{
    private final Occurrences occurrences;

    public OccurrencesJson( final Occurrences occurrences )
    {
        this.occurrences = occurrences;
    }

    @JsonIgnore
    public Occurrences getOccurrences()
    {
        return occurrences;
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
