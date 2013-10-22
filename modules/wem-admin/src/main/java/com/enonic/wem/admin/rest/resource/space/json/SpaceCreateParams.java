package com.enonic.wem.admin.rest.resource.space.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpaceCreateParams
{

    private String spaceName;

    private String displayName;

    private String iconReference;

    public String getSpaceName()
    {
        return spaceName;
    }

    public void setSpaceName( final String spaceName )
    {
        this.spaceName = spaceName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public String getIconReference()
    {
        return iconReference;
    }

    public void setIconReference( final String iconReference )
    {
        this.iconReference = iconReference;
    }
}
