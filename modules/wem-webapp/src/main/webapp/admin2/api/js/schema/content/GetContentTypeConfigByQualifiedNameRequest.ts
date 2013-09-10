module api_schema_content {

    export class GetContentTypeConfigByQualifiedNameRequest extends ContentTypeResourceRequest {

        private qualifiedName:string;

        constructor(qualifiedName:string) {
            super();
            super.setMethod("GET");
            this.qualifiedName = qualifiedName;
        }

        getUrl():string {
            var resourceUrl = super.getResourceUrl();
            return resourceUrl + "config?qualifiedName=" + this.qualifiedName;
        }
    }
}