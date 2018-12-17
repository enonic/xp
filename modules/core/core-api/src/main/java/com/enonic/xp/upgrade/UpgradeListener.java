package com.enonic.xp.upgrade;

public interface UpgradeListener
{
    void total( long total );

    void upgraded();

    void finished();
}
