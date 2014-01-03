module api.module {

    export class ListModuleRequest extends ModuleResourceRequest<ModuleListResult> {

        constructor()
        {
            super();
            super.setMethod("GET");
        }

        getParams():Object {
            return {};
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }
}