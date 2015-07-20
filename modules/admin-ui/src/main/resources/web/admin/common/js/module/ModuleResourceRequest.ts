module api.module {

    export class ModuleResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "module");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToModule(json: api.module.json.ModuleJson): Application {
            return Application.fromJson(json);
        }
    }
}