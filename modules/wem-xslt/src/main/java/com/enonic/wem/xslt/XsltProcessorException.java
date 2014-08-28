package com.enonic.wem.xslt;

import java.util.Formatter;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import com.enonic.wem.xslt.internal.XsltProcessorErrors;

public final class XsltProcessorException
    extends RuntimeException
{
    private final XsltProcessorErrors errors;

    public XsltProcessorException( final Throwable cause, final XsltProcessorErrors errors )
    {
        super( cause.getMessage(), cause );
        this.errors = errors;
    }

    public XsltProcessorErrors getErrors()
    {
        return this.errors;
    }

    @Override
    public Throwable getCause()
    {
        return null;
    }

    @Override
    public String getMessage()
    {
        return formatMessage( super.getMessage(), this.errors );
    }

    private static String formatMessage( final String heading, final XsltProcessorErrors errors )
    {
        if ( !errors.hasErrors() )
        {
            return heading;
        }

        final Formatter fmt = new Formatter().format( heading ).format( ":%n" );

        int index = 1;
        for ( final TransformerException error : errors )
        {
            if ( error.getLocationAsString() != null )
            {
                fmt.format( "%s) %s (%s)%n", index++, error.getMessage(), getLocation( error.getLocator() ) );
            }
            else
            {
                fmt.format( "%s) %s (%s)%n", index++, error.getMessage(), "?" );
            }
        }

        return fmt.toString();
    }

    private static String getLocation( final SourceLocator locator )
    {
        final StringBuilder str = new StringBuilder();
        str.append( locator.getSystemId() );

        if ( locator.getLineNumber() >= 0 )
        {
            str.append( " #" ).append( locator.getLineNumber() );
        }

        return str.toString();
    }
}
