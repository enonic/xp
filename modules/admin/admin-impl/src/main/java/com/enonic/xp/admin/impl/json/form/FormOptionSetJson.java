package com.enonic.xp.admin.impl.json.form;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.form.FormOptionSet;

public class FormOptionSetJson
    extends FormItemJson<FormOptionSet>
{
    private final FormOptionSet formOptionSet;

    private final OccurrencesJson occurrences;

    private final List<FormOptionSetOptionJson> options;

    public FormOptionSetJson( final FormOptionSet formOptionSet )
    {
        this.formOptionSet = formOptionSet;
        this.occurrences = new OccurrencesJson( formOptionSet.getOccurrences() );
        this.options = formOptionSet.getOptions().stream().map( FormOptionSetOptionJson::new ).collect( Collectors.toList() );
    }

    public String getName()
    {
        return formOptionSet.getName();
    }

    public String getLabel()
    {
        return formOptionSet.getLabel();
    }

    public boolean isExpanded()
    {
        return formOptionSet.isExpanded();
    }

    public OccurrencesJson getOccurrences()
    {
        return occurrences;
    }

    public List<FormOptionSetOptionJson> getOptions()
    {
        return this.options;
    }

    @JsonIgnore
    @Override
    public FormOptionSet getFormItem()
    {
        return getFormOptionSet();
    }

    @JsonIgnore
    public FormOptionSet getFormOptionSet()
    {
        return formOptionSet;
    }

}

