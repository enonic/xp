package com.enonic.wem.admin.rest.resource.relationship.json;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RelationshipUpdateParams
{
    private RelationshipKeyParam relationshipKey;

    private Map<String, String> add;

    private List<String> remove;

    public RelationshipUpdateParams()
    {
    }

    public RelationshipUpdateParams( String str )
    {
    }

    public RelationshipKeyParam getRelationshipKey()
    {
        return relationshipKey;
    }

    public void setRelationshipKey( final RelationshipKeyParam relationshipKey )
    {
        this.relationshipKey = relationshipKey;
    }

    public Map<String, String> getAdd()
    {
        return add;
    }

    public void setAdd( final Map<String, String> add )
    {
        this.add = add;
    }

    public List<String> getRemove()
    {
        return remove;
    }

    public void setRemove( final List<String> remove )
    {
        this.remove = remove;
    }
}
