module api.schema.content {

    export class ContentTypeResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourceUrl: api.rest.Path;

        constructor() {
            super();
            this.resourceUrl = api.rest.Path.fromParent(super.getRestPath(), "schema/content");
        }

        getResourcePath(): api.rest.Path {
            return this.resourceUrl;
        }

        fromJsonToContentType(json: api.schema.content.ContentTypeJson): ContentType {
            return ContentType.fromJson(json);
        }

        fromJsonToContentTypeSummary(json: api.schema.content.ContentTypeSummaryJson): ContentTypeSummary {
            return ContentTypeSummary.fromJson(json);
        }
    }
}