package com.enonic.wem.core.schema.content.serializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.core.support.serializer.XmlParsingException;

public final class FormItemsXmlSerializer
{
    private FormItemXmlSerializer formItemSerializer = new FormItemXmlSerializer( this );

    public void serialize( final Iterable<FormItem> formItems, final Element parentEl )
    {
        for ( FormItem formItem : formItems )
        {
            parentEl.addContent( formItemSerializer.serialize( formItem ) );
        }
    }

    public Element serialize( final Iterable<FormItem> formItems )
    {
        Element itemsEl = new Element( "items" );
        for ( FormItem formItem : formItems )
        {
            itemsEl.addContent( formItemSerializer.serialize( formItem ) );
        }
        return itemsEl;
    }

    public Iterable<FormItem> parse( final Element parentEl )
    {
        final List<FormItem> formItems = new ArrayList<>();
        final Iterator formItemIt = parentEl.getChildren().iterator();
        while ( formItemIt.hasNext() )
        {
            final Element formItemEl = (Element) formItemIt.next();

            try
            {
                formItems.add( formItemSerializer.parse( formItemEl ) );
            }
            catch ( Exception e )
            {
                throw new XmlParsingException( "Failed to parse FormItem: " + e.getMessage(), e );
            }
        }

        return formItems;
    }

    public FormItemXmlSerializer getFormItemXmlSerializer()
    {
        return formItemSerializer;
    }
}
