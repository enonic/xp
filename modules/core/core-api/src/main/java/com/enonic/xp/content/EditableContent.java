package com.enonic.xp.content;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.EditablePage;

@PublicApi
public final class EditableContent
{
    public final Content source;

    public String displayName;

    public PropertyTree data;

    public ExtraDatas extraDatas;

    public EditablePage page;

    public WorkflowInfo workflowInfo;

    public EditableContent( final Content source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.data = source.getData().copy();
        this.extraDatas = source.getAllExtraData().copy();
        this.page = source.getPage() != null ? new EditablePage( source.getPage() ) : null;
        this.workflowInfo = source.getWorkflowInfo();
    }

    public Content build()
    {
        return Content.create( this.source )
            .displayName( displayName )
            .data( data )
            .extraDatas( extraDatas ).page( page != null ? page.build() : null )
            .workflowInfo( workflowInfo )
            .build();
    }
}
