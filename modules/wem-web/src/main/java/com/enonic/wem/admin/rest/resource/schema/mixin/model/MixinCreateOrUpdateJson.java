package com.enonic.wem.admin.rest.resource.schema.mixin.model;

public class MixinCreateOrUpdateJson
{
    private final boolean created;

    private MixinCreateOrUpdateJson( boolean created )
    {
        this.created = created;
    }

    public static MixinCreateOrUpdateJson created()
    {
        return new MixinCreateOrUpdateJson( true );
    }

    public static MixinCreateOrUpdateJson updated()
    {
        return new MixinCreateOrUpdateJson( false );
    }

    public boolean isCreated()
    {
        return created;
    }

    public boolean isUpdated()
    {
        return !created;
    }
}
