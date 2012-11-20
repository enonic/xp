package com.enonic.wem.api.content.type.form;

public final class MinimumOccurrencesException
    extends RuntimeException
{
    public MinimumOccurrencesException( final Input input, final int size )
    {
        super( buildMessage( input, size ) );
    }

    public MinimumOccurrencesException( final Input input, final BreaksRequiredContractException e, final int occurrences )
    {
        super( buildMessage( input, e, occurrences ), e );
    }

    private static String buildMessage( final Input input, final BreaksRequiredContractException e, final int occurrences )
    {
        return "Input [" + input + "] requires minimum " + input.getOccurrences().getMinimum() +
            " occurrences. It had " + occurrences + ", but: " + e.getMessage();
    }

    private static String buildMessage( final Input input, final int size )
    {
        return "Input [" + input + "] requires minimum " + input.getOccurrences().getMinimum() +
            " occurrences: " + size;
    }
}
