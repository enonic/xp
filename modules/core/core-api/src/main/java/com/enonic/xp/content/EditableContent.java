package com.enonic.xp.content;


import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.EditablePage;
import com.enonic.xp.page.Page;

@PublicApi
public final class EditableContent
{
    @NonNull
    public final Content source;

    public String displayName;

    public PropertyTree data;

    public ExtraDatas extraDatas;

    public WorkflowInfo workflowInfo;

    private EditablePage page;

    /**
     * Assigns an editable page to the content using the provided {@link Page}.
     * If the given page is null, the currently associated editable page will be set to null.
     * Otherwise, a new {@link EditablePage} will be created using the provided {@link Page}.
     *
     * @param page the {@link Page} to be associated with the content; may be null
     * @return the newly created {@link EditablePage} if a non-null {@link Page} is provided,
     * or null if the input {@link Page} is null
     */
    @NullUnmarked
    public EditablePage page( final Page page )
    {
        return page == null ? this.page = null : ( this.page = new EditablePage( page ) );
    }

    /**
     * Returns the editable page of the content.
     * If no page is set, a new editable page will be created.
     *
     * @return the editable page of the content
     */
    @NonNull
    public EditablePage page()
    {
        return this.page == null ? this.page = new EditablePage() : this.page;
    }

    /**
     * Removes the page from the content.
     * The effect of this call is equivalent to that of calling {@link #page(Page)} {@code page(null)} on this object.
     */
    public void withoutPage()
    {
        this.page( null );
    }

    public EditableContent( @NonNull final Content source )
    {
        this.source = Objects.requireNonNull( source );
        this.displayName = source.getDisplayName();
        this.data = source.getData().copy();
        this.extraDatas = source.getAllExtraData().copy();
        this.page = this.page( source.getPage() );
        this.workflowInfo = source.getWorkflowInfo();
    }

    @NonNull
    public Content build()
    {
        return Content.create( this.source )
            .displayName( displayName )
            .data( data )
            .extraDatas( extraDatas )
            .page( page != null ? page.build() : null )
            .workflowInfo( workflowInfo )
            .build();
    }
}
