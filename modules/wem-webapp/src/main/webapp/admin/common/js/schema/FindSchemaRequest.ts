module api.schema {

    export class FindSchemaRequest extends SchemaResourceRequest {

        private search:string;

        private modules:string[];

        private types:string[];

        constructor(search?:string) {
            super();
            super.setMethod("POST");
            if (search) {
                this.search = search;
            }
        }

        public setSearch(search:string) {
            this.search = search;
            return this;
        }

        public setTypes(types:string[]) {
            this.types = types;
            return this;
        }

        public setModules(modules:string[]) {
            this.modules = modules;
            return this;
        }

        getParams():Object {
            return {
                search: this.search || '',
                types: this.types || [],
                modules: this.modules || []
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "find");
        }
    }
}