package com.enonic.wem.export.internal;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFile;

class NodeImportPathResolver {
    private static final boolean IS_WINDOWS = System.getProperty("os.name").contains("indow");

    public static NodePath resolveNodeImportPath(final VirtualFile parent, final VirtualFile exportRoot, final NodePath importRoot) {

        final Path parentPath = Paths.get(removeLeadingWindowsSlash(parent.getUrl().getPath()));

        final Path exportRootPath = Paths.get(removeLeadingWindowsSlash(exportRoot.getUrl().getPath()));

        final Path relativePath = exportRootPath.relativize(parentPath);

        final NodePath.Builder builder = NodePath.newPath(importRoot);

        relativePath.forEach((path) -> builder.addElement(path.toString()));

        return builder.build();
    }

    private static String removeLeadingWindowsSlash(final String value) {

        if (!IS_WINDOWS) {
            return value;
        }

        return value.replaceFirst("^/+([A-Z]:)", "$1");
    }
}