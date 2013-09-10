module api_schema_content {

    export class GetContentTypeByQualifiedNameRequest extends ContentTypeResourceRequest {

        private qualifiedName:string;

        private mixinReferencesToFormItems:boolean = true;

        constructor(qualifiedName:string) {
            super();
            super.setMethod("GET");
            this.qualifiedName = qualifiedName;
        }

        setMixinReferencesToFormItems(value:boolean):GetContentTypeByQualifiedNameRequest {
            this.mixinReferencesToFormItems = value;
            return this;
        }

        getUrl():string {
            var resourceUrl = super.getResourceUrl();
            return resourceUrl + "?qualifiedName=" + this.qualifiedName + "&mixinReferencesToFormItems=" + this.mixinReferencesToFormItems;
        }
    }
}