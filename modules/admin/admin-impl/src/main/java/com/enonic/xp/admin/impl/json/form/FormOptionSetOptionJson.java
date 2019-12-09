package com.enonic.xp.admin.impl.json.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.FormOptionSetOption;

import static com.google.common.base.Strings.nullToEmpty;

public class FormOptionSetOptionJson
{
    private final FormOptionSetOption formOptionSetOption;

    private final List<FormItemJson> items;

    private final LocaleMessageResolver localeMessageResolver;

    public FormOptionSetOptionJson( final FormOptionSetOption formOptionSetOption, final LocaleMessageResolver localeMessageResolver )
    {
        Preconditions.checkNotNull( formOptionSetOption );
        Preconditions.checkNotNull( localeMessageResolver );

        this.formOptionSetOption = formOptionSetOption;
        this.localeMessageResolver = localeMessageResolver;

        this.items = wrapFormItems( formOptionSetOption.getFormItems(), localeMessageResolver );
    }

    private List<FormItemJson> wrapFormItems( final FormItems items, final LocaleMessageResolver localeMessageResolver )
    {
        final List<FormItemJson> formItemJsonList = new ArrayList<>();
        for ( FormItem formItem : items )
        {
            formItemJsonList.add( FormItemJsonFactory.create( formItem, localeMessageResolver ) );
        }
        return formItemJsonList;
    }

    public String getName()
    {
        return formOptionSetOption.getName();
    }

    public String getLabel()
    {
        if ( localeMessageResolver != null && !nullToEmpty( formOptionSetOption.getLabelI18nKey() ).isBlank() )
        {
            return localeMessageResolver.localizeMessage( formOptionSetOption.getLabelI18nKey(), formOptionSetOption.getLabel() );
        }
        else
        {
            return formOptionSetOption.getLabel();
        }
    }

    public String getHelpText()
    {
        if ( localeMessageResolver != null && !nullToEmpty( formOptionSetOption.getHelpTextI18nKey() ).isBlank() )
        {
            return localeMessageResolver.localizeMessage( formOptionSetOption.getHelpTextI18nKey(), formOptionSetOption.getHelpText() );
        }
        else
        {
            return formOptionSetOption.getHelpText();
        }
    }

    public boolean isDefaultOption()
    {
        return formOptionSetOption.isDefaultOption();
    }

    public List<FormItemJson> getItems()
    {
        return items;
    }

    @JsonIgnore
    public FormOptionSetOption getFormOptionSetOption()
    {
        return formOptionSetOption;
    }
}
