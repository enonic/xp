package com.enonic.xp.dump;

public class SystemDumpUpgradeParams
{
    private String dumpName;

    public SystemDumpUpgradeParams( final Builder builder )
    {
        this.dumpName = builder.dumpName;
    }

    public String getDumpName()
    {
        return dumpName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String dumpName;

        private Builder()
        {
        }

        public Builder dumpName( final String dumpName )
        {
            this.dumpName = dumpName;
            return this;
        }

        public SystemDumpUpgradeParams build()
        {
            return new SystemDumpUpgradeParams( this );
        }
    }
}
