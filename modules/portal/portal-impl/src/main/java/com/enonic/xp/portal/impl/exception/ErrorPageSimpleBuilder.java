package com.enonic.xp.portal.impl.exception;

import com.enonic.xp.portal.impl.html.HtmlBuilder;

public class ErrorPageSimpleBuilder
    implements ErrorPageBuilder
{
    private int statusCode;

    private String title;

    private String tip;

    private String logoutUrl;

    public ErrorPageSimpleBuilder status( final int value )
    {
        this.statusCode = value;
        return this;
    }

    public ErrorPageSimpleBuilder title( final String value )
    {
        this.title = value;
        return this;
    }

    public ErrorPageSimpleBuilder tip( final String value )
    {
        this.tip = value;
        return this;
    }

    public ErrorPageSimpleBuilder logoutUrl( final String logoutUrl )
    {
        this.logoutUrl = logoutUrl;
        return this;
    }

    @Override
    public String build()
    {
        final HtmlBuilder html = new HtmlBuilder();
        html.text( "<!DOCTYPE html>" );
        html.open( "html" );
        html.open( "head" ).open( "title" ).escapedText( this.statusCode + " - " + this.title ).close();
        html.open( "style" )
            .text( "html, body { height: 100%; } " + "body { font-family: Arial, Helvetica, sans-serif; " +
                       "margin: 0; display: flex; flex-direction: column; justify-content: center; align-items: center; color: lightgray; } " +
                       "h1 { font-size: 3em; margin: 0; } h3 { font-size: 1.5em; }" +
                       ( this.logoutUrl != null ? " .logout { color: lightgray; }" : "" ) )
            .close();
        html.close();
        final HtmlBuilder htmlBuilder = html.open( "body" );
        htmlBuilder.open( "h1" ).escapedText( "D'oh!" ).close();
        htmlBuilder.open( "h3" ).escapedText( this.statusCode + " - " + this.title ).close();
        if ( tip != null )
        {
            htmlBuilder.open( "h4" ).escapedText( tip ).close();
        }

        if ( this.logoutUrl != null )
        {
            htmlBuilder.open( "a" ).attribute( "href", this.logoutUrl ).attribute( "class", "logout" ).escapedText( "Logout" ).close();
        }

        html.close();
        html.close();
        return html.toString();
    }
}
