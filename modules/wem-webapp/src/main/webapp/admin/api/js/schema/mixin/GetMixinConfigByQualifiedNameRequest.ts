module api_schema_mixin {

    export class GetMixinConfigByQualifiedNameRequest extends MixinResourceRequest<GetMixinConfigResult> {

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
            return api_rest.Path.fromParent(super.getResourcePath(), "config");
        }
    }
}