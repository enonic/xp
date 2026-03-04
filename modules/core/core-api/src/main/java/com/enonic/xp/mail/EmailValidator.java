package com.enonic.xp.mail;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import com.enonic.xp.annotation.PublicApi;

/**
 * Validate that the email address conforms to the syntax rules of RFC 822.
 */
@PublicApi
public final class EmailValidator
{
    public static boolean isValid( final String email )
    {
        try
        {
            InternetAddress emailAddress = new InternetAddress( email );
            emailAddress.validate();
            return true;
        }
        catch ( AddressException ex )
        {
            return false;
        }
    }
}
