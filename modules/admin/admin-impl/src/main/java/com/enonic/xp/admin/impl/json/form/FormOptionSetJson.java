package com.enonic.xp.admin.impl.json.form;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.form.FormOptionSet;

public class FormOptionSetJson
    extends FormItemJson<FormOptionSet>
{
    private final FormOptionSet formOptionSet;

    private final OccurrencesJson occurrences;

    private final OccurrencesJson multiselection;

    private final List<FormOptionSetOptionJson> options;

    public FormOptionSetJson( final FormOptionSet formOptionSet )
    {
        this.formOptionSet = formOptionSet;
        this.occurrences = new OccurrencesJson( formOptionSet.getOccurrences() );
        this.multiselection = new OccurrencesJson( formOptionSet.getMultiselection() );
        this.options = StreamSupport.stream( formOptionSet.spliterator(), false ).
            map( FormOptionSetOptionJson::new ).
            collect( Collectors.toList() );
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

    public OccurrencesJson getMultiselection()
    {
        return multiselection;
    }

    public List<FormOptionSetOptionJson> getOptions()
    {
        return this.options;
    }

    public String getHelpText()
    {
        return formOptionSet.getHelpText();
    }

    @JsonIgnore
    @Override
    public FormOptionSet getFormItem()
    {
        return formOptionSet;
    }

}

