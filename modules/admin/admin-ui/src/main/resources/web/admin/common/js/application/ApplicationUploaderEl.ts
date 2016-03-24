module api.application {

    import ValueTypes = api.data.ValueTypes;

    export class ApplicationUploaderEl extends api.ui.uploader.UploaderEl<Application> {

        private failure: string;

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


        createModel(serverResponse: api.application.json.ApplicationInstallResultJson): Application {
            if (serverResponse) {

                let result = ApplicationInstallResult.fromJson(serverResponse);

                this.failure = result.getFailure();

                return result.getApplication();
            }
            else {
                return null;
            }
        }

        getFailure(): string {
            return this.failure;
        }

        getModelValue(item: Application): string {
            return item.getId();
        }

        createResultItem(value: string): api.dom.Element {
            return new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('application/' + value), "_blank");
        }

        protected getErrorMessage(fileString: string): string {
            return "The application could not be installed";
        }
    }
}