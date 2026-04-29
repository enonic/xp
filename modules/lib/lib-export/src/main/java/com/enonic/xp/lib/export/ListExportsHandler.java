package com.enonic.xp.lib.export;

import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ListExportsResult;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class ListExportsHandler
    implements ScriptBean
{
    private BeanContext context;

    public ListExportsResultMapper execute()
    {
        final ListExportsResult result = this.context.getService( ExportService.class ).get().list();
        return new ListExportsResultMapper( result );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
    }
}
