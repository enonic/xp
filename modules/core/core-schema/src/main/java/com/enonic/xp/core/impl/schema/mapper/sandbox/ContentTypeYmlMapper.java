package com.enonic.xp.core.impl.schema.mapper.sandbox;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.content.ContentType;

public class ContentTypeYmlMapper
{
    @JsonProperty("name")
    public String name;

    @JsonProperty("form")
    public FormYml form;

    @JsonProperty("displayName")
    public DisplayName displayName;

    public static class DisplayName
    {
        @JsonProperty("text")
        public String text;

        @JsonProperty("i18n")
        public String i18n;

        @JsonProperty("expression")
        public String expression;
    }

    public ContentType.Builder toContentTypeBuilder()
    {
        final ContentType.Builder builder = ContentType.create()
            .displayName( displayName.text )
            .displayNameI18nKey( displayName.i18n )
            .displayNameExpression( displayName.expression );

        if ( form != null )
        {
            List<FormItem> formItems = form.formItems.stream().map( item -> {
                if ( item instanceof InputYml input )
                {
                    return InputRegistry.toInput( input );
                }
                if ( item instanceof ItemSetYml itemSet )
                {
                    return itemSet.toFormItemSet();
                }
                return null;
            } ).filter( Objects::nonNull ).toList();
            builder.form( Form.create().addFormItems( formItems ).build() );
        }

        return builder;
    }
}
