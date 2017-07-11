package com.enonic.xp.repo.impl.dump.reader;

class EntryLoadError
{
    private final String message;

    private EntryLoadError( final String message )
    {
        this.message = message;
    }

    static EntryLoadError error( final String message )
    {
        return new EntryLoadError( message );
    }

    public String getMessage()
    {
        return message;
    }
}
