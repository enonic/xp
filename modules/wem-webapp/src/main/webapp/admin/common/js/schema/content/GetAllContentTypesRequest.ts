module api_schema_content {

    export class GetAllContentTypesRequest extends ContentTypeResourceRequest<api_schema_content_json.ContentTypeSummaryListJson> {

        private mixinReferencesToFormItems:boolean = true;

        constructor() {
            super();
            super.setMethod("GET");
        }

        setMixinReferencesToFormItems(value:boolean):GetAllContentTypesRequest {
            this.mixinReferencesToFormItems = value;
            return this;
        }

        getParams():Object {
            return {
                mixinReferencesToFormItems: this.mixinReferencesToFormItems
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "all");
        }
    }
}