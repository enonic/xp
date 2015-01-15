package com.enonic.wem.core.form;

import com.enonic.wem.api.data.PropertySet;
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

    public void toData( final Form form, final PropertySet parent )
    {
        final PropertySet formAsSet = parent.addSet( dataSetName );
        formItemsDataSerializer.toData( form.getFormItems(), formAsSet );
    }

    public Form fromData( final PropertySet formAsPropertySet )
    {
        final Form.Builder form = Form.newForm();
        final FormItems formItems = formItemsDataSerializer.fromData( formAsPropertySet.getProperties() );
        form.addFormItems( formItems );
        return form.build();
    }
}

