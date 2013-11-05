package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.Page;

public class GetPage
    extends Command<Page>
{
    private ContentId contentId;

    public GetPage content( ContentId value )
    {
        this.contentId = value;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
