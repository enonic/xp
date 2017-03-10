package com.enonic.xp.core.impl.issue;

import com.enonic.xp.issue.Issue;
import com.enonic.xp.node.Node;

public interface IssueNodeTranslator
{
    Issue fromNode( Node node );
}
