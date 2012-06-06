package com.enonic.wem.web.rest.account;

class CsvBuilder
{
    private final static String DEFAULT_SEPARATOR = ",";

    private String separator;

    private boolean currentLineIsEmpty;

    private final StringBuilder sb;

    public CsvBuilder()
    {
        separator = DEFAULT_SEPARATOR;
        sb = new StringBuilder();
        currentLineIsEmpty = true;
    }

    public CsvBuilder addValue( String value )
    {
        if ( !currentLineIsEmpty )
        {
            sb.append( separator );
        }

        if ( value != null )
        {
            if ( needsQuoteEncoding( value ) )
            {
                value = getQuotedText( value );
            }
            sb.append( value );
        }

        currentLineIsEmpty = false;

        return this;
    }

    public String build()
    {
        return sb.toString();
    }

    public CsvBuilder endOfLine()
    {
        sb.append( "\r\n" );
        currentLineIsEmpty = true;
        return this;
    }

    public String getSeparator()
    {
        return separator;
    }

    public CsvBuilder setSeparator( String separator )
    {
        this.separator = separator;
        return this;
    }

    private boolean needsQuoteEncoding( String text )
    {
        return text.contains( "\r" ) || text.contains( "\n" ) || text.contains( "\t" ) || text.contains( "\"" ) ||
            text.contains( separator );
    }

    private String getQuotedText( String text )
    {
        String quoted = text.replace( "\"", "\"\"" );
        quoted = "\"" + quoted + "\"";
        return quoted;
    }
}
