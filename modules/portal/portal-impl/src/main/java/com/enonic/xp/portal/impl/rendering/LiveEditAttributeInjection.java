package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.region.ComponentType;

final class LiveEditAttributeInjection
{
    private final static char BYTE_ORDER_MARK = '\uFEFF';

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
        p = skipByteOrderMark( responseHtml, p );
        p = skipXmlDeclaration( responseHtml, p );
        p = skipDocType( responseHtml, p );
        p = skipComments( responseHtml, p );

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

    private int skipByteOrderMark( final String responseHtml, final int initialPosition )
    {
        if ( initialPosition > responseHtml.length() - 1 )
        {
            return initialPosition;
        }
        char ch = responseHtml.charAt( initialPosition );
        if ( ch == BYTE_ORDER_MARK )
        {
            return initialPosition + 1;
        }
        return initialPosition;
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

    private int skipDocType( final String responseHtml, final int initialPosition )
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
        if ( ch != '<' || p < responseHtml.length() - 7 && !"!DOCTYPE".equals( responseHtml.substring( p, p + 8 ) ) )
        {
            return initialPosition;
        }

        final int endOfXmlDeclaration = responseHtml.indexOf( '>', p + 1 );
        return endOfXmlDeclaration == -1 ? initialPosition : endOfXmlDeclaration + 1;
    }

    private int skipComment( final String responseHtml, final int initialPosition )
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

        final int endOfXmlComment = responseHtml.indexOf( "-->", p + 1 );
        return endOfXmlComment == -1 ? initialPosition : endOfXmlComment + 3;
    }

    private int skipComments( final String responseHtml, final int initialPosition )
    {
        int p;
        int newP = initialPosition;

        do
        {
            p = newP;
            newP = skipComment( responseHtml, p );
        }
        while ( p != newP );

        return newP;
    }

}
