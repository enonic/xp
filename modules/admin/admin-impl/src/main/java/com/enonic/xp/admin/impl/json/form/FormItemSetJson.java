package com.enonic.xp.admin.impl.json.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormItems;

import static com.google.common.base.Strings.nullToEmpty;

@SuppressWarnings("UnusedDeclaration")
public class FormItemSetJson
    extends FormItemJson<FormItemSet>
{
    private final FormItemSet formItemSet;

    private final List<FormItemJson> items;

    private final OccurrencesJson occurrences;

    private final LocaleMessageResolver localeMessageResolver;

    public FormItemSetJson( final FormItemSet formItemSet, final LocaleMessageResolver localeMessageResolver )
    {
        Preconditions.checkNotNull( formItemSet );
        Preconditions.checkNotNull( localeMessageResolver );

        this.formItemSet = formItemSet;
        this.localeMessageResolver = localeMessageResolver;

        this.items = wrapFormItems( formItemSet.getFormItems(), localeMessageResolver );
        this.occurrences = new OccurrencesJson( formItemSet.getOccurrences() );
    }

    private static Iterable<FormItem> unwrapFormItems( final List<FormItemJson> items )
    {
        final List<FormItem> formItems = new ArrayList<>( items.size() );
        for ( FormItemJson formItemJson : items )
        {
            formItems.add( formItemJson.getFormItem() );
        }
        return formItems;
    }

    private static List<FormItemJson> wrapFormItems( final FormItems items, final LocaleMessageResolver localeMessageResolver )
    {
        final List<FormItemJson> formItemJsonList = new ArrayList<>();
        for ( FormItem formItem : items )
        {
            formItemJsonList.add( FormItemJsonFactory.create( formItem, localeMessageResolver ) );
        }
        return formItemJsonList;
    }

    @JsonIgnore
    @Override
    public FormItemSet getFormItem()
    {
        return getFormItemSet();
    }

    @JsonIgnore
    public FormItemSet getFormItemSet()
    {
        return formItemSet;
    }

    @Override
    public String getName()
    {
        return formItemSet.getName();
    }

    public String getLabel()
    {
        if ( localeMessageResolver != null && !nullToEmpty( formItemSet.getLabelI18nKey() ).isBlank() )
        {
            return localeMessageResolver.localizeMessage( formItemSet.getLabelI18nKey(), formItemSet.getLabel() );
        }
        else
        {
            return formItemSet.getLabel();
        }
    }

    public boolean isImmutable()
    {
        return formItemSet.isImmutable();
    }

    public String getCustomText()
    {
        return formItemSet.getCustomText();
    }

    public String getHelpText()
    {
        if ( localeMessageResolver != null && !nullToEmpty( formItemSet.getHelpTextI18nKey() ).isBlank() )
        {
            return localeMessageResolver.localizeMessage( formItemSet.getHelpTextI18nKey(), formItemSet.getHelpText() );
        }
        else
        {
            return formItemSet.getHelpText();
        }
    }

    public List<FormItemJson> getItems()
    {
        return items;
    }

    public OccurrencesJson getOccurrences()
    {
        return occurrences;
    }
}
