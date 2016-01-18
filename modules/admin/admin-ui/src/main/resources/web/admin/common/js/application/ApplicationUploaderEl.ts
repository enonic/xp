module api.application {

    import ValueTypes = api.data.ValueTypes;

    export class ApplicationUploaderEl extends api.ui.uploader.UploaderEl<Application> {

        constructor(config: api.ui.uploader.UploaderElConfig) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("application/install");
            }

            if (config.allowTypes == undefined) {
                config.allowTypes = [{title: 'Application files', extensions: 'jar,zip'}];
            }

            super(config);

            this.addClass('media-uploader-el');
        }


        createModel(serverResponse: api.application.json.ApplicationJson): Application {
            if (serverResponse) {
                return new api.application.ApplicationBuilder().
                    fromJson(<api.application.json.ApplicationJson> serverResponse).
                    build();
            }
            else {
                return null;
            }
        }

        getModelValue(item: Application): string {
            return item.getId();
        }

        createResultItem(value: string): api.dom.Element {
            return new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('application/' + value), "_blank");
        }
    }
}