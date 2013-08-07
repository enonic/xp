package com.enonic.wem.admin.rest.resource.schema.model;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.support.tree.TreeNode;

public class SchemaTreeNodeJson
    extends SchemaJson
{

    private boolean hasChildren;

    private List<SchemaTreeNodeJson> schemas;

    public SchemaTreeNodeJson( final TreeNode<Schema> schema )
    {
        super( schema.getObject() );
        setHasChildren( schema.hasChildren() );
        schemas = new ArrayList<>();
        for ( TreeNode<Schema> node : schema.getChildren() )
        {
            schemas.add( new SchemaTreeNodeJson( node ) );
        }
    }

    public boolean isHasChildren()
    {
        return hasChildren;
    }

    public void setHasChildren( final boolean hasChildren )
    {
        this.hasChildren = hasChildren;
    }

    public List<SchemaTreeNodeJson> getSchemas()
    {
        return schemas;
    }

    public void setSchemas( final List<SchemaTreeNodeJson> schemas )
    {
        this.schemas = schemas;
    }
}
