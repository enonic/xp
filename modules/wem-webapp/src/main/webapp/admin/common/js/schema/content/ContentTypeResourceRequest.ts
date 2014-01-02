module api.schema.content {

    export class ContentTypeResourceRequest<T> extends api.rest.ResourceRequest<T> {

        private resourceUrl:api.rest.Path;

        constructor() {
            super();
            this.resourceUrl = api.rest.Path.fromParent(super.getRestPath(), "schema/content");
        }

        getResourcePath():api.rest.Path {
            return this.resourceUrl;
        }

        fromJsonToContentType(json:api.schema.content.json.ContentTypeJson):ContentType {
            return new ContentType(json);
        }
    }
}