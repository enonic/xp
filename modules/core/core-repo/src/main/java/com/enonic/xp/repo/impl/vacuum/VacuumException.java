package com.enonic.xp.repo.impl.vacuum;

public class VacuumException
    extends RuntimeException
{
    public VacuumException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
