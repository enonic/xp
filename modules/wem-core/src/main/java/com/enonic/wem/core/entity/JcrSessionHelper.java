package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.util.Exceptions;

class JcrSessionHelper
{
    static void save( final Session session )
    {
        try
        {
            session.save();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error while saving to JCR" ).withCause( e );
        }
    }

}
