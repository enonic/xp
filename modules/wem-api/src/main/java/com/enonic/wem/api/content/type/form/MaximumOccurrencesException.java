package com.enonic.wem.api.content.type.form;

public final class MaximumOccurrencesException
    extends RuntimeException
{
    public MaximumOccurrencesException( final Input input, final int size )
    {
        super( buildMessage( input, size ) );
    }

    public MaximumOccurrencesException( final FormItemSet set, final int size )
    {
        super( buildMessage( set, size ) );
    }

    private static String buildMessage( final Input input, final int size )
    {

        String message = "Input [" + input + "] allows maximum " + input.getOccurrences().getMaximum();
        if ( input.getOccurrences().getMaximum() < 2 )
        {
            return message +
                " occurrence: " + size;
        }
        else
        {
            return message +
                " occurrences: " + size;
        }
    }

    private static String buildMessage( final FormItemSet set, final int size )
    {

        String message = "FormItemSet [" + set + "] allows maximum " + set.getOccurrences().getMaximum();
        if ( set.getOccurrences().getMaximum() < 2 )
        {
            return message +
                " occurrence: " + size;
        }
        else
        {
            return message +
                " occurrences: " + size;
        }
    }
}
