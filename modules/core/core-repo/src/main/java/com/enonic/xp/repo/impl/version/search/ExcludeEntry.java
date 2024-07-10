package com.enonic.xp.repo.impl.version.search;

import com.enonic.xp.node.NodePath;

public record ExcludeEntry(NodePath nodePath, boolean recursive)
{
}
