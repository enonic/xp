module api.application {

    export class ApplicationResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "application");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToApplication(json: api.application.json.ApplicationJson): Application {
            return Application.fromJson(json);
        }
    }
}