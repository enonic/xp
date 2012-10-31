package com.enonic.wem.core.content.type.formitem;


import java.util.Iterator;

import org.jdom.Element;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.core.content.JsonParsingException;

import com.enonic.cms.framework.util.JDOMUtil;

public class ComponentsSerializerXml
{
    private ComponentSerializerXml componentSerializer = new ComponentSerializerXml( this );

    public Element generate( Components components )
    {
        Element itemsEl = new Element( "items" );
        for ( Component component : components.iterable() )
        {
            itemsEl.addContent( componentSerializer.generate( component ) );
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
                throw new JsonParsingException( "Failed to parse Component: " + JDOMUtil.printElement( componentEl ), e );
            }
        }

        return components;
    }
}
