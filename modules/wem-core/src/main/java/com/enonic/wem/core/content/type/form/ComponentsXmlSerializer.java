package com.enonic.wem.core.content.type.form;


import java.util.Iterator;

import org.jdom.Element;

import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.core.content.XmlParsingException;

import com.enonic.cms.framework.util.JDOMUtil;

public final class ComponentsXmlSerializer
{
    private ComponentXmlSerializer componentSerializer = new ComponentXmlSerializer( this );

    public Element serialize( final Iterable<FormItem> components )
    {
        Element itemsEl = new Element( "items" );
        for ( FormItem formItem : components )
        {
            itemsEl.addContent( componentSerializer.serialize( formItem ) );
        }
        return itemsEl;
    }

    public FormItems parse( final Element parentEl )
    {
        final FormItems formItems = new FormItems();
        final Element itemsEl = parentEl.getChild( "items" );
        final Iterator componentIt = itemsEl.getChildren().iterator();
        while ( componentIt.hasNext() )
        {
            final Element componentEl = (Element) componentIt.next();

            try
            {
                formItems.add( componentSerializer.parse( componentEl ) );
            }
            catch ( Exception e )
            {
                throw new XmlParsingException( "Failed to parse FormItem: " + JDOMUtil.printElement( componentEl ), e );
            }
        }

        return formItems;
    }
}
