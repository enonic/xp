package com.enonic.xp.upgrade;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.upgrade.model.UpgradeModel;
import com.enonic.xp.upgrade.model.UpgradeModel001;
import com.enonic.xp.upgrade.model.UpgradeModel002;
import com.enonic.xp.upgrade.model.UpgradeModel003;
import com.enonic.xp.upgrade.model.UpgradeModel004;
import com.enonic.xp.upgrade.model.UpgradeModel005;
import com.enonic.xp.upgrade.model.UpgradeModel006;
import com.enonic.xp.upgrade.model.UpgradeModel007;

final class UpgradeTaskLocator
{
    private final List<UpgradeModel> upgradeModels;

    public UpgradeTaskLocator()
    {
        this.upgradeModels = Lists.newArrayList();
        this.upgradeModels.add( new UpgradeModel001() );
        this.upgradeModels.add( new UpgradeModel002() );
        this.upgradeModels.add( new UpgradeModel003() );
        this.upgradeModels.add( new UpgradeModel004() );
        this.upgradeModels.add( new UpgradeModel005() );
        this.upgradeModels.add( new UpgradeModel006() );
        this.upgradeModels.add( new UpgradeModel007() );
    }

    public List<UpgradeModel> getUpgradeModels()
    {
        return upgradeModels;
    }
}
