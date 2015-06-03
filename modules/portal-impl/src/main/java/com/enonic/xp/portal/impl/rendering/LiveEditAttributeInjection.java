package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.content.page.region.ComponentType;
import com.enonic.xp.portal.PortalResponse;

final class LiveEditAttributeInjection
{
    public LiveEditAttributeInjection()
    {
    }

    public PortalResponse injectLiveEditAttribute( final PortalResponse response, final ComponentType componentType )
    {
        final Object bodyObj = response.getBody();
        if ( !( bodyObj instanceof String ) )
        {
            return response;
        }

        final String responseHtml = (String) bodyObj;
        int p = 0;
        p = skipXmlDeclaration( responseHtml, p );
        p = skipDocTypeOrComment( responseHtml, p );

        char ch = ' ';
        while ( p < responseHtml.length() )
        {
            ch = responseHtml.charAt( p );
            p++;
            if ( ch == '<' || !Character.isWhitespace( ch ) )
            {
                break;
            }
        }
        if ( ch != '<' )
        {
            return response; // no opening tag found, live edit attribute cannot be injected
        }

        int startAttrPos = 0;
        while ( p < responseHtml.length() )
        {
            ch = responseHtml.charAt( p );
            if ( ch == '>' || ch == '/' || Character.isWhitespace( ch ) )
            {
                startAttrPos = p;
                break;
            }
            p++;
        }

        if ( startAttrPos > 0 )
        {
            final String liveEditAttribute = " " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"" + componentType.toString() + "\"";
            final String injectedHtml = new StringBuilder( responseHtml ).insert( startAttrPos, liveEditAttribute ).toString();
            return PortalResponse.create( response ).body( injectedHtml ).build();
        }
        return response;
    }

    private int skipXmlDeclaration( final String responseHtml, final int initialPosition )
    {
        int p = initialPosition;
        char ch = ' ';
        while ( p < responseHtml.length() )
        {
            ch = responseHtml.charAt( p );
            p++;
            if ( ch == '<' || !Character.isWhitespace( ch ) )
            {
                break;
            }
        }
        if ( ch != '<' || ( p < responseHtml.length() && responseHtml.charAt( p ) != '?' ) )
        {
            return initialPosition;
        }

        final int endOfXmlDeclaration = responseHtml.indexOf( '>', p + 1 );
        return endOfXmlDeclaration == -1 ? initialPosition : endOfXmlDeclaration + 1;
    }

    private int skipDocTypeOrComment( final String responseHtml, final int initialPosition )
    {
        int p = initialPosition;
        char ch = ' ';
        while ( p < responseHtml.length() )
        {
            ch = responseHtml.charAt( p );
            p++;
            if ( ch == '<' || !Character.isWhitespace( ch ) )
            {
                break;
            }
        }
        if ( ch != '<' || ( p < responseHtml.length() && responseHtml.charAt( p ) != '!' ) )
        {
            return initialPosition;
        }

        final int endOfXmlDeclaration = responseHtml.indexOf( '>', p + 1 );
        return endOfXmlDeclaration == -1 ? initialPosition : endOfXmlDeclaration + 1;
    }

}
