package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;

public class NodeImportResultJson
{
    private boolean dryRun;

    private List<String> addedNodes = Lists.newArrayList();

    private List<String> updateNodes = Lists.newArrayList();

    private List<String> importErrors = Lists.newArrayList();

    private List<String> importedBinaries = Lists.newArrayList();

    public static NodeImportResultJson from( final NodeImportResult result )
    {
        final NodeImportResultJson json = new NodeImportResultJson();

        json.dryRun = result.isDryRun();

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

        json.importedBinaries.addAll( result.getExportedBinaries() );

        return json;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isDryRun()
    {
        return dryRun;
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
        final ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writeValueAsString( this );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }
}
