package com.enonic.xp.mail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailValidator
{
    /**
     * Validate that this address conforms to the syntax rules of RFC 822.
     */
    public static boolean validate( String email )
    {
        boolean result = true;
        try
        {
            InternetAddress emailAddr = new InternetAddress( email );
            emailAddr.validate();
        }
        catch ( AddressException ex )
        {
            result = false;
        }
        return result;
    }
}
