package com.enonic.wem.web.rest.service.account;

public final class UserIdGenerator
{
    private final static int MAXIMUM_LENGTH = 8;


    private final String firstName;

    private final String lastName;

    private String suffix;

    private int counter;

    private int iterations;

    private int currentIteration;

    public UserIdGenerator( final String firstName, final String lastName )
    {
        if ( firstName == null || lastName == null )
        {
            throw new IllegalArgumentException( "First name and last name cannot be null" );
        }
        if ( firstName.length() == 0 && lastName.length() == 0 )
        {
            throw new IllegalArgumentException( "First name and last name cannot be empty" );
        }

        final AsciiLettersTextFilter asciiLettersFilter = new AsciiLettersTextFilter();
        if ( firstName.length() == 0 )
        {
            this.firstName = latinToAZ( asciiLettersFilter, lastName );
            this.lastName = "";
        }
        else
        {
            this.firstName = latinToAZ( asciiLettersFilter, firstName );
            this.lastName = latinToAZ( asciiLettersFilter, lastName );
        }

        initialize();
    }

    private void initialize()
    {
        suffix = "";
        counter = 0;
        resetIteration();
    }

    private void resetIteration()
    {
        iterations = lastName.length() + firstName.length() - 1;
        if ( ( iterations + 1 ) > ( MAXIMUM_LENGTH - suffix.length() ) )
        {
            iterations -= iterations + 1 - ( MAXIMUM_LENGTH - suffix.length() );
        }
        currentIteration = 1;
    }

    public String nextUserName()
    {
        final int lettersFromLastName = Math.min( Math.min( currentIteration, lastName.length() ), MAXIMUM_LENGTH - 1 - suffix.length() );
        final int lettersFromFirstName = Math.min( firstName.length(), MAXIMUM_LENGTH - lettersFromLastName - suffix.length() ) -
            Math.max( 0, currentIteration - lettersFromLastName );

        final String userName = firstName.substring( 0, lettersFromFirstName ) + lastName.substring( 0, lettersFromLastName ) + suffix;

        currentIteration++;
        if ( currentIteration > iterations )
        {
            counter++;
            suffix = Integer.toString( counter );
            resetIteration();
        }

        return userName;
    }

    private String latinToAZ( final AsciiLettersTextFilter asciiLettersFilter, String text )
    {
        return asciiLettersFilter.convertUnicodeToAsciiLetters( text ).toLowerCase();
    }

}
