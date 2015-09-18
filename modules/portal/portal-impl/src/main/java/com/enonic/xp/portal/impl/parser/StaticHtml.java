package com.enonic.xp.portal.impl.parser;

public final class StaticHtml
    extends HtmlBlock
{
    private final String html;

    public StaticHtml( final String html )
    {
        this.html = html;
    }

    public String getHtml()
    {
        return html;
    }

    @Override
    public String toString()
    {
        return html;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final StaticHtml that = (StaticHtml) o;
        return html.equals( that.html );
    }

    @Override
    public int hashCode()
    {
        return html.hashCode();
    }
}
