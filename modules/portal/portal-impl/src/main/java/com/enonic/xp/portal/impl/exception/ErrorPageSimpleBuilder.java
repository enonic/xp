package com.enonic.xp.portal.impl.exception;

public class ErrorPageSimpleBuilder
    implements ErrorPageBuilder
{
    private int statusCode;

    private String title;

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

    public String build()
    {
        final HtmlBuilder html = new HtmlBuilder();
        html.text( "<!DOCTYPE html>" );
        html.open( "html" );
        html.open( "head" ).open( "title" ).escapedText( this.statusCode + " - " + this.title ).close();
        html.open( "style" ).text( "html, body { height: 100%; } " + "body { font-family: Arial, Helvetica, sans-serif; " +
                                       "margin: 0; display: flex; flex-direction: column; justify-content: center; align-items: center; color: lightgray; } " +
                                       "h1 { font-size: 3em; margin: 0; } h3 { font-size: 1.5em; }" ).close();
        html.close();
        html.open( "body" ).
            open( "h1" ).escapedText( "D'oh!" ).close().
            open( "h3" ).escapedText( this.statusCode + " - " + this.title ).
            close();
        html.close();
        html.close();
        return html.toString();
    }
}
