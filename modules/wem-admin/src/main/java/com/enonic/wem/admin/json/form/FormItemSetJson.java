package com.enonic.wem.admin.json.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.FormItems;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;

@SuppressWarnings("UnusedDeclaration")
public class FormItemSetJson
    extends FormItemJson<FormItemSet>
{
    private final FormItemSet formItemSet;

    private final List<FormItemJson> items;

    private final OccurrencesJson occurrences;

    @JsonCreator
    public FormItemSetJson( @JsonProperty("name") String name, @JsonProperty("label") String label,
                            @JsonProperty("customText") String customText, @JsonProperty("helpText") String helpText,
                            @JsonProperty("validationRegexp") String validationRegexp, @JsonProperty("immutable") boolean immutable,
                            @JsonProperty("inputType") InputTypeJson inputType, @JsonProperty("occurrences") OccurrencesJson occurrences,
                            @JsonProperty("items") List<FormItemJson> items )
    {
        formItemSet =  newFormItemSet().
            name( name ).
            label( label ).
            immutable( immutable ).
            customText( customText ).
            helpText( helpText ).
            occurrences( occurrences.getOccurrences() ).
            addFormItems( unwrapFormItems( items ) ).
            build() ;
        this.items = items;
        this.occurrences = occurrences;
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


    public FormItemSetJson( final FormItemSet formItemSet )
    {
        this.formItemSet = formItemSet;
        this.items = wrapFormItems( formItemSet.getFormItems() );
        this.occurrences = new OccurrencesJson( formItemSet.getOccurrences() );
    }

    private static List<FormItemJson> wrapFormItems( final FormItems items )
    {
        final List<FormItemJson> formItemJsonList = new ArrayList<>();
        for ( FormItem formItem : items )
        {
            formItemJsonList.add( FormItemJsonFactory.create( formItem ) );
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
        return formItemSet.getLabel();
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
        return formItemSet.getHelpText();
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
