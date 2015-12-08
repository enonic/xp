package com.enonic.xp.admin.impl.rest.resource.launcher.json;


import java.util.Set;

public final class AdminApplicationDescriptorJson
{
    public String key;

    public String name;

    public String shortName;

    public String icon;

    public AdminApplicationIconJson iconImage;

    public Set<String> allowedPrincipals;
}
