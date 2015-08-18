module api.content.page {

    export class PageResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToContent(json: api.content.json.ContentJson): api.content.Content {
            return api.content.Content.fromJson(json);
        }
    }
}