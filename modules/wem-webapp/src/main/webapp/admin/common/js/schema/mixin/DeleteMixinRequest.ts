module api_schema_mixin {

    export class DeleteMixinRequest extends MixinResourceRequest<api_schema_mixin_json.MixinJson> {

        private qualifiedNames: string[] = [];

        constructor(qualifiedNames?:string[]) {
            super();
            super.setMethod("POST");
            if (qualifiedNames) {
                this.setQualifiedNames(qualifiedNames);
            }
        }

        setQualifiedNames(qualifiedNames:string[]):DeleteMixinRequest {
            this.qualifiedNames = qualifiedNames;
            return this;
        }

        addQualifiedName(qualifiedName:string):DeleteMixinRequest {
            this.qualifiedNames.push(qualifiedName);
            return this;
        }

        getParams():Object {
            return {
                qualifiedNames: this.qualifiedNames
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "delete");
        }
    }
}