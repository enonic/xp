package com.enonic.wem.admin.rest.resource.space.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpaceUpdateParams
    extends SpaceCreateParams
{
    private String newName;

    public String getNewName()
    {
        return newName;
    }

    public void setNewName( final String newName )
    {
        this.newName = newName;
    }
}
