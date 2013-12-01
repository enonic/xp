module api_schema_mixin {

    export class DeleteMixinRequest extends MixinResourceRequest<api_schema_mixin_json.MixinJson> {

        private names: string[] = [];

        constructor(names?:string[]) {
            super();
            super.setMethod("POST");
            if (names) {
                this.setNames(names);
            }
        }

        setNames(names:string[]):DeleteMixinRequest {
            this.names = names;
            return this;
        }

        addName(name:MixinName):DeleteMixinRequest {
            this.names.push(name.toString());
            return this;
        }

        getParams():Object {
            return {
                names: this.names
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "delete");
        }
    }
}