package com.enonic.xp.repo.impl.index;

public class UpdateIndexSettings
{
    private final String settings;

    private UpdateIndexSettings( final String settings )
    {
        this.settings = settings;
    }

    public static UpdateIndexSettings from( final String settings )
    {
        return new UpdateIndexSettings( settings );
    }

    public String getSettingsAsString()
    {
        return settings;
    }
}
