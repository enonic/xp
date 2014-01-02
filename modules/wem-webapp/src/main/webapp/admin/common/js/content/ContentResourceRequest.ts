module api.content {

    export class ContentResourceRequest<T> extends api.rest.ResourceRequest<T>{

        public static EXPAND_NONE = 'none';
        public static EXPAND_SUMMARY = 'summary';
        public static EXPAND_FULL = 'full';

        private resourcePath:api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content");
        }

        getResourcePath():api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToContent(json:api.content.json.ContentJson):Content {
            return new Content(json);
        }
    }
}