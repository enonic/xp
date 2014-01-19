package com.enonic.wem.core.form;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.support.serializer.AbstractDataListSerializer;

public class FormItemsDataSerializer
    extends AbstractDataListSerializer<FormItems, FormItems>
{
    private final FormItemDataSerializer formItemDataSerializer = new FormItemDataSerializer();

    public List<Data> toData( final FormItems formItems )
    {
        final List<Data> formItemsAsData = new ArrayList<>();
        for ( FormItem formItem : formItems )
        {
            formItemsAsData.add( formItemDataSerializer.toData( formItem ) );
        }
        return formItemsAsData;
    }

    public FormItems fromData( final List<Data> dataList )
    {
        FormItems formItems = new FormItems();
        for ( Data data : dataList )
        {
            formItems.add( formItemDataSerializer.fromData( data.toDataSet() ) );
        }
        return formItems;
    }

}

