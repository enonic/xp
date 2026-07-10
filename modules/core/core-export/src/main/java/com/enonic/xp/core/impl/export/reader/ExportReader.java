package com.enonic.xp.core.impl.export.reader;

import java.util.stream.Stream;

import com.enonic.xp.vfs.VirtualFile;

import static com.enonic.xp.core.impl.export.ExportConstants.NODE_XML_EXPORT_NAME;
import static com.enonic.xp.core.impl.export.ExportConstants.SYSTEM_FOLDER_NAME;

public class ExportReader
{
    public int getNodeFileCount( final VirtualFile virtualFile )
    {
        int nodeFileCount = virtualFile.getChildren().stream().mapToInt( this::getNodeFileCount ).sum();
        if ( NODE_XML_EXPORT_NAME.equals( virtualFile.getName() ) )
        {
            nodeFileCount++;
        }
        return nodeFileCount;
    }

    public Stream<VirtualFile> getChildren( final VirtualFile parent )
    {
        return parent.getChildren()
            .stream()
            .filter( VirtualFile::isFolder )
            .filter( ( folder ) -> !folder.getName().equals( SYSTEM_FOLDER_NAME ) );
    }
}
