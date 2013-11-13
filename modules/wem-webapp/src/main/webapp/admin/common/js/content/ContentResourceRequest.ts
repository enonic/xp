module api_content {

    export class ContentResourceRequest<T> extends api_rest.ResourceRequest<T>{

        public static EXPAND_NONE = 'none';
        public static EXPAND_SUMMARY = 'summary';
        public static EXPAND_FULL = 'full';

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}