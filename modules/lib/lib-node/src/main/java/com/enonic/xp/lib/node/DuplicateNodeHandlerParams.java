package com.enonic.xp.lib.node;

import java.util.Objects;

import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.script.ScriptValue;

public class DuplicateNodeHandlerParams
{
    private NodeId nodeId;

    private String name;

    private NodePath parent;

    private boolean includeChildren;

    private NodeDataProcessor dataProcessor;

    private RefreshMode refresh;

    public void setNodeId( final String nodeId )
    {
        this.nodeId = NodeId.from( Objects.requireNonNull( nodeId ) );
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setParent( final String parent )
    {
        this.parent = parent != null ? new NodePath( parent ) : null;
    }

    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    public void setDataProcessor( final ScriptValue processor )
    {
        this.dataProcessor = processor != null ? ( originalData, path ) -> {
            PropertyTreeMapper mapper = new PropertyTreeMapper( originalData );
            return new ScriptValueTranslator().create( processor.call( mapper ) ).getPropertyTree();
        } : null;
    }

    public void setRefresh( final String refresh )
    {
        this.refresh = refresh != null ? RefreshMode.valueOf( refresh ) : null;
    }

    public NodeId nodeId()
    {
        return nodeId;
    }

    public String name()
    {
        return name;
    }

    public NodePath parent()
    {
        return parent;
    }

    public boolean includeChildren()
    {
        return includeChildren;
    }

    public NodeDataProcessor dataProcessor()
    {
        return dataProcessor;
    }

    public RefreshMode refresh()
    {
        return refresh;
    }
}
