package com.enonic.xp.admin.impl.json.form;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.form.FormOptionSet;

import static com.google.common.base.Strings.nullToEmpty;

public class FormOptionSetJson
    extends FormItemJson<FormOptionSet>
{
    private final FormOptionSet formOptionSet;

    private final OccurrencesJson occurrences;

    private final OccurrencesJson multiselection;

    private final List<FormOptionSetOptionJson> options;

    private final LocaleMessageResolver localeMessageResolver;

    public FormOptionSetJson( final FormOptionSet formOptionSet, final LocaleMessageResolver localeMessageResolver )
    {
        Preconditions.checkNotNull( formOptionSet );
        Preconditions.checkNotNull( localeMessageResolver );

        this.formOptionSet = formOptionSet;
        this.localeMessageResolver = localeMessageResolver;

        this.occurrences = new OccurrencesJson( formOptionSet.getOccurrences() );
        this.multiselection = new OccurrencesJson( formOptionSet.getMultiselection() );
        this.options = StreamSupport.stream( formOptionSet.spliterator(), false ).
            map( formOptionSetOption -> new FormOptionSetOptionJson( formOptionSetOption, localeMessageResolver ) ).
            collect( Collectors.toList() );
    }

    @Override
    public String getName()
    {
        return formOptionSet.getName();
    }

    public String getLabel()
    {
        if ( localeMessageResolver != null && !nullToEmpty( formOptionSet.getLabelI18nKey() ).isBlank() )
        {
            return localeMessageResolver.localizeMessage( formOptionSet.getLabelI18nKey(), formOptionSet.getLabel() );
        }
        else
        {
            return formOptionSet.getLabel();
        }
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
        if ( localeMessageResolver != null && !nullToEmpty( formOptionSet.getHelpTextI18nKey() ).isBlank() )
        {
            return localeMessageResolver.localizeMessage( formOptionSet.getHelpTextI18nKey(), formOptionSet.getHelpText() );
        }
        else
        {
            return formOptionSet.getHelpText();
        }
    }

    @JsonIgnore
    @Override
    public FormOptionSet getFormItem()
    {
        return formOptionSet;
    }

}

