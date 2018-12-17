package com.enonic.xp.dump;

import com.enonic.xp.upgrade.UpgradeListener;

public class SystemDumpUpgradeParams
{
    private String dumpName;

    private UpgradeListener upgradeListener;

    public SystemDumpUpgradeParams( final Builder builder )
    {
        this.dumpName = builder.dumpName;
        this.upgradeListener = builder.upgradeListener;
    }

    public String getDumpName()
    {
        return dumpName;
    }

    public UpgradeListener getUpgradeListener()
    {
        return upgradeListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String dumpName;

        private UpgradeListener upgradeListener;

        private Builder()
        {
        }

        public Builder dumpName( final String dumpName )
        {
            this.dumpName = dumpName;
            return this;
        }

        public Builder upgradeListener( final UpgradeListener upgradeListener )
        {
            this.upgradeListener = upgradeListener;
            return this;
        }

        public SystemDumpUpgradeParams build()
        {
            return new SystemDumpUpgradeParams( this );
        }
    }
}
