module api_module {

    export class ListModuleRequest extends ModuleResourceRequest<ModuleListResult> {

        constructor()
        {
            super();
            super.setMethod("GET");
        }

        getParams():Object {
            return {};
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }
}