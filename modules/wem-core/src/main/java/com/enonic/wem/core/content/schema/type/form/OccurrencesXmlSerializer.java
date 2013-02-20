package com.enonic.wem.core.content.schema.type.form;


import org.jdom.Attribute;
import org.jdom.Element;

import com.enonic.wem.api.content.schema.type.form.Occurrences;

import static com.enonic.wem.api.content.schema.type.form.Occurrences.newOccurrences;

class OccurrencesXmlSerializer
{
    public Element serialize( final Occurrences occurrences )
    {
        Element occurrencesEl = new Element( "occurrences" );
        if ( null != occurrences )
        {
            occurrencesEl.setAttribute( "minimum", String.valueOf( occurrences.getMinimum() ) );
            occurrencesEl.setAttribute( "maximum", String.valueOf( occurrences.getMaximum() ) );
        }
        return occurrencesEl;
    }

    public Occurrences parse( final Element parentEl )
    {
        Element occurrencesEl = parentEl.getChild( "occurrences" );
        if ( occurrencesEl == null )
        {
            return null;
        }

        int minimum = 0;
        int maximum = 0;

        Attribute minimumAttr = occurrencesEl.getAttribute( "minimum" );
        Attribute maximumAttr = occurrencesEl.getAttribute( "maximum" );

        if ( minimumAttr != null )
        {
            minimum = Integer.parseInt( minimumAttr.getValue() );
        }

        if ( maximumAttr != null )
        {
            maximum = Integer.parseInt( maximumAttr.getValue() );
        }

        if ( maximumAttr == null && minimumAttr == null )
        {
            return null;
        }

        return newOccurrences().minimum( minimum ).maximum( maximum ).build();
    }
}
