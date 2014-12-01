package com.enonic.wem.export.internal.reader;

import com.enonic.wem.export.internal.writer.ExportItemPath;
import com.enonic.wem.export.internal.writer.ExportItemPaths;

public interface ExportReader
{
    public ExportItemPaths getChildrenPaths( final ExportItemPath parent );

    public String getItem( final ExportItemPath path );

}
