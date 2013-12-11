module api_content_site {

    export class SiteResourceRequest<T> extends api_rest.ResourceRequest<T>{

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "site");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }

        fromJsonToContent(json:api_content_json.ContentJson):api_content.Content {
            return new api_content.Content(json);
        }
    }
}