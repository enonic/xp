package com.enonic.xp.impl.server.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.impl.server.rest.ModelToStringHelper;
import com.enonic.xp.node.NodePath;

public class NodeImportResultJson
{
    private final List<String> addedNodes = new ArrayList<>();

    private final List<String> updateNodes = new ArrayList<>();

    private final List<String> importErrors = new ArrayList<>();

    private final List<String> importedBinaries = new ArrayList<>();

    public static NodeImportResultJson from( final NodeImportResult result )
    {
        final NodeImportResultJson json = new NodeImportResultJson();

        for ( final NodePath nodePath : result.getAddedNodes() )
        {
            json.addedNodes.add( nodePath.toString() );
        }

        for ( final NodePath nodePath : result.getUpdateNodes() )
        {
            json.updateNodes.add( nodePath.toString() );
        }

        for ( final NodeImportResult.ImportError importError : result.getImportErrors() )
        {
            json.importErrors.add( importError.getMessage() + " - " + importError.getException() );
        }

        json.importedBinaries.addAll( result.getImportedBinaries() );

        return json;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public boolean isDryRun()
    {
        return false;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getAddedNodes()
    {
        return addedNodes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getUpdateNodes()
    {
        return updateNodes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getImportErrors()
    {
        return importErrors;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getImportedBinaries()
    {
        return importedBinaries;
    }

    @Override
    public String toString()
    {
        return ModelToStringHelper.convertToString( this );
    }
}
