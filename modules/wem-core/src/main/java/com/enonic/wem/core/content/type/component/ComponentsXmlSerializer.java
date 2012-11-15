package com.enonic.wem.core.content.type.component;


import java.util.Iterator;

import org.jdom.Element;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.core.content.XmlParsingException;

import com.enonic.cms.framework.util.JDOMUtil;

public final class ComponentsXmlSerializer
{
    private ComponentXmlSerializer componentSerializer = new ComponentXmlSerializer( this );

    public Element serialize( final Iterable<Component> components )
    {
        Element itemsEl = new Element( "items" );
        for ( Component component : components )
        {
            itemsEl.addContent( componentSerializer.serialize( component ) );
        }
        return itemsEl;
    }

    public Components parse( final Element parentEl )
    {
        final Components components = new Components();
        final Element itemsEl = parentEl.getChild( "items" );
        final Iterator componentIt = itemsEl.getChildren().iterator();
        while ( componentIt.hasNext() )
        {
            final Element componentEl = (Element) componentIt.next();

            try
            {
                components.add( componentSerializer.parse( componentEl ) );
            }
            catch ( Exception e )
            {
                throw new XmlParsingException( "Failed to parse Component: " + JDOMUtil.printElement( componentEl ), e );
            }
        }

        return components;
    }
}
