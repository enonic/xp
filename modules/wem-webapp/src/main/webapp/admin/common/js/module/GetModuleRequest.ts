module api_module {

    export class GetModuleRequest extends ModuleResourceRequest<any> {

        private key:string;

        constructor(key:string) {
            super();
            super.setMethod("GET");
            this.key = key;
        }

        getParams():Object {
            return {
                key: this.key
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "get");
        }
    }
}