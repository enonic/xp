package com.enonic.xp.upgrade;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.upgrade.model.UpgradeModel;
import com.enonic.xp.upgrade.model.UpgradeModel001;
import com.enonic.xp.upgrade.model.UpgradeModel002;
import com.enonic.xp.upgrade.model.UpgradeModel003;

public final class UpgradeTaskLocator
{
    private final List<UpgradeModel> upgradeModels;

    public UpgradeTaskLocator()
    {
        this.upgradeModels = Lists.newArrayList();
        this.upgradeModels.add( new UpgradeModel001() );
        this.upgradeModels.add( new UpgradeModel002() );
        this.upgradeModels.add( new UpgradeModel003() );
    }

    public List<UpgradeModel> getUpgradeModels()
    {
        return upgradeModels;
    }
}
