module app.launcher {

    export class LauncherResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "launcher");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToApplication(json: app.launcher.json.AdminApplicationJson): api.app.Application {
            return new api.app.Application(json.key.name, json.name, json.shortName, json.iconUrl);
        }
    }
}