package com.enonic.xp.admin.impl.rest.resource.security.json;

@SuppressWarnings("UnusedDeclaration")
public final class EmailAvailabilityJson
{
    private final boolean available;

    public EmailAvailabilityJson( final boolean isAvailable )
    {
        this.available = isAvailable;

    }

    public boolean isAvailable()
    {
        return available;
    }
}
