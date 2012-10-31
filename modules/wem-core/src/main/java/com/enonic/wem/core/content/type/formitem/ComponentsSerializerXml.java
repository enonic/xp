package com.enonic.wem.core.content.type.formitem;


import java.util.Iterator;

import org.jdom.Element;

import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.Components;
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
        final Iterator formItemIt = itemsEl.getChildren().iterator();
        while ( formItemIt.hasNext() )
        {
            final Element formItemEl = (Element) formItemIt.next();

            try
            {
                components.add( componentSerializer.parse( formItemEl ) );
            }
            catch ( Exception e )
            {
                throw new JsonParsingException( "Failed to parse Component: " + JDOMUtil.printElement( formItemEl ), e );
            }
        }

        return components;
    }
}
