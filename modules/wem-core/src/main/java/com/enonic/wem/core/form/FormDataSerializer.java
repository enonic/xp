package com.enonic.wem.core.form;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

public class FormDataSerializer
    extends AbstractDataSetSerializer<Form, Form>
{
    private final String dataSetName;

    private final FormItemsDataSerializer formItemsDataSerializer = new FormItemsDataSerializer();

    public FormDataSerializer( final String dataSetName )
    {
        this.dataSetName = dataSetName;
    }

    public DataSet toData( final Form form )
    {
        final DataSet asData = new DataSet( dataSetName );
        for ( Data data : formItemsDataSerializer.toData( form.getFormItems() ) )
        {
            asData.add( data );
        }
        return asData;
    }

    public Form fromData( final DataSet dataSet )
    {
        final Form.Builder form = Form.newForm();
        final FormItems formItems = formItemsDataSerializer.fromData( dataSet.datas() );
        form.addFormItems( formItems );
        return form.build();
    }
}

