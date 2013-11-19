module api_module {

    export class ModuleResourceRequest<T> extends api_rest.ResourceRequest<T>{

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "module");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}