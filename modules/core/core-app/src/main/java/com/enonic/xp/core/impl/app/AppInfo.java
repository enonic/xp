package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationType;

public class AppInfo
{
    public String name;

    public ApplicationType type = ApplicationType.BUNDLE;

    public boolean hasCmsDescriptor;

    public String title;

    public String vendorName;

    public String version;

    public String maxSystemVersion;

    public String minSystemVersion;

    public boolean system;
}
