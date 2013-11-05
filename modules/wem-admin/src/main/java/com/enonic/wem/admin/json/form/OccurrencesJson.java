package com.enonic.wem.admin.json.form;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.form.Occurrences;

@SuppressWarnings("UnusedDeclaration")
public class OccurrencesJson
{
    private final Occurrences occurrences;

    @JsonCreator
    public OccurrencesJson( @JsonProperty("minimum") int minimum, @JsonProperty("maximum") int maximum )
    {
        occurrences = new Occurrences( minimum, maximum );
    }

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
