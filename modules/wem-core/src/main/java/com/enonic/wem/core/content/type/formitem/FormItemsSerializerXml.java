package com.enonic.wem.core.content.type.formitem;


import java.util.Iterator;

import org.jdom.Element;

import com.enonic.wem.api.content.type.formitem.FormItem;
import com.enonic.wem.api.content.type.formitem.FormItems;
import com.enonic.wem.core.content.JsonParsingException;

import com.enonic.cms.framework.util.JDOMUtil;

public class FormItemsSerializerXml
{
    private FormItemSerializerXml formItemSerializer = new FormItemSerializerXml( this );

    public Element generate( FormItems formItems )
    {
        Element itemsEl = new Element( "items" );
        for ( FormItem formItem : formItems.iterable() )
        {
            itemsEl.addContent( formItemSerializer.generate( formItem ) );
        }
        return itemsEl;
    }

    public FormItems parse( final Element parentEl )
    {
        final FormItems formItems = new FormItems();
        final Element itemsEl = parentEl.getChild( "items" );
        final Iterator formItemIt = itemsEl.getChildren().iterator();
        while ( formItemIt.hasNext() )
        {
            final Element formItemEl = (Element) formItemIt.next();

            try
            {
                formItems.addFormItem( formItemSerializer.parse( formItemEl ) );
            }
            catch ( Exception e )
            {
                throw new JsonParsingException( "Failed to parse FormItem: " + JDOMUtil.printElement( formItemEl ), e );
            }
        }

        return formItems;
    }
}
