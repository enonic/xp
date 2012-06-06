package com.enonic.wem.web.rest.account;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserDao;

public class UserIdGenerator
{
    private final static int MAXIMUM_LENGTH = 8;

    private final static int MAXIMUM_SUFFIX_COUNT = 100;


    private final UserDao userDao;

    private final AsciiLettersTextFilter asciiLettersFilter;

    private int maximumLength;

    private int maximumSuffixCount;

    public UserIdGenerator( UserDao userDao )
    {
        this.userDao = userDao;
        this.asciiLettersFilter = new AsciiLettersTextFilter();
        this.maximumLength = MAXIMUM_LENGTH;
        this.maximumSuffixCount = MAXIMUM_SUFFIX_COUNT;
    }

    public String generateUserId( String firstName, String lastName, UserStoreKey userStoreKey )
    {
        if ( firstName == null || lastName == null )
        {
            return null;
        }

        if ( firstName.length() == 0 && lastName.length() == 0 )
        {
            return null;
        }

        if ( firstName.length() == 0 )
        {
            firstName = lastName;
            lastName = "";
        }

        final String first = latinToAZ( firstName ).toLowerCase();
        final String last = latinToAZ( lastName ).toLowerCase();

        String suffix = "";
        int counter = 0;
        boolean done = false;
        String newUID = null;

        while ( !done )
        {

            int iterations = last.length() + first.length() - 1;
            if ( ( iterations + 1 ) > ( maximumLength - suffix.length() ) )
            {
                iterations -= iterations + 1 - ( maximumLength - suffix.length() );
            }

            for ( int i = 1; i <= iterations; i++ )
            {
                int lettersFromSname = Math.min( Math.min( i, last.length() ), maximumLength - 1 - suffix.length() );
                int lettersFromFname =
                    Math.min( first.length(), maximumLength - lettersFromSname - suffix.length() ) - Math.max( 0, i - lettersFromSname );

                newUID = first.substring( 0, lettersFromFname ) + last.substring( 0, lettersFromSname ) + suffix;

                if ( !userExists( newUID, userStoreKey ) )
                {
                    done = true;
                    break;
                }
                else
                {
                    newUID = null;
                }
            }
            counter++;
            suffix = Integer.toString( counter );

            // Not very likely to happen, exit to prevent infinite loop
            if ( counter == maximumSuffixCount )
            {
                return null;
            }
        }

        return newUID;
    }

    private String latinToAZ( String text )
    {
        return asciiLettersFilter.convertUnicodeToAsciiLetters( text );
    }

    private boolean userExists( String uid, UserStoreKey userStoreKey )
    {
        QualifiedUsername qualifiedUsername = new QualifiedUsername( userStoreKey, uid );
        UserEntity user = userDao.findByQualifiedUsername( qualifiedUsername );
        return user != null;
    }

    public int getMaximumLength()
    {
        return maximumLength;
    }

    public void setMaximumLength( int maximumLength )
    {
        this.maximumLength = maximumLength;
    }

    public int getMaximumSuffixCount()
    {
        return maximumSuffixCount;
    }

    public void setMaximumSuffixCount( int maximumSuffixCount )
    {
        this.maximumSuffixCount = maximumSuffixCount;
    }

}
