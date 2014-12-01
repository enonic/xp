package com.enonic.wem.core.form;

import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.PropertySet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemType;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.support.serializer.AbstractDataListSerializer;

public class FormItemsDataSerializer
    extends AbstractDataListSerializer<FormItems, FormItems>
{
    public void toData( final FormItems formItems, final PropertySet parent )
    {
        for ( final FormItem formItem : formItems )
        {
            new FormItemDataSerializer( formItem.getType() ).toData( formItem, parent );
        }
    }

    public FormItems fromData( final Iterable<Property> formItemsProperties )
    {
        final FormItems formItems = new FormItems();
        for ( final Property formItemProperty : formItemsProperties )
        {
            formItems.add( new FormItemDataSerializer( FormItemType.parse( formItemProperty.getName() ) ).
                fromData( formItemProperty.getSet() ) );
        }
        return formItems;
    }

}

