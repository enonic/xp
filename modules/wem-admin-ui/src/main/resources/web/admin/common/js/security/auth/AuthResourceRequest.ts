module api.security.auth {

    export class AuthResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "auth");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

    }
}