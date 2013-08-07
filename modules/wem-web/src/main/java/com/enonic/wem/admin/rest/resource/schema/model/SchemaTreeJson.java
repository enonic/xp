package com.enonic.wem.admin.rest.resource.schema.model;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;

public class SchemaTreeJson
{

    private int total;

    private List<SchemaTreeNodeJson> schemas;

    public SchemaTreeJson( Tree<Schema> tree )
    {
        this.total = tree.deepSize();
        this.schemas = new ArrayList<>();
        for ( TreeNode<Schema> node : tree )
        {
            schemas.add( new SchemaTreeNodeJson( node ) );
        }
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( final int total )
    {
        this.total = total;
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
