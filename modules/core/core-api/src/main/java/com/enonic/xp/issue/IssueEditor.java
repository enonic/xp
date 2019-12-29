package com.enonic.xp.issue;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface IssueEditor
{
    void edit( final EditableIssue edit );
}