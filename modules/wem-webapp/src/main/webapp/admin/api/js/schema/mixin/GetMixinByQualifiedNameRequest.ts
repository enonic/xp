module api_schema_mixin {

    export class GetMixinByQualifiedNameRequest extends MixinResourceRequest {
        private qualifiedName:string;

        constructor(qualifiedName:string) {
            super();
            super.setMethod("GET");
            this.qualifiedName = qualifiedName;
        }

        getParams():Object {
            return {
                qualifiedName: this.qualifiedName
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}