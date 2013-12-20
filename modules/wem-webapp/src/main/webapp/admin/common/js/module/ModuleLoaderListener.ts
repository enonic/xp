module api_module
{
    export class ModuleLoaderListener
    {
        onLoading: () => void;

        onLoaded: (modules:api_module.ModuleSummary[]) => void;
    }
}