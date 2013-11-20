module api_module {

    export class InstallModuleRequest extends ModuleResourceRequest<any> {

        private uploader:any;
        private triggerElement:api_dom.Element;

        private deferred:JQueryDeferred<api_rest.Response>;

        constructor() {
            super();
            this.triggerElement = new api_dom.ButtonEl("trigger-el");
            this.triggerElement.hide();
            this.deferred = jQuery.Deferred<api_rest.Response>();
            api_dom.Body.get().appendChild(this.triggerElement);
            this.uploader = this.createUploader(this.triggerElement);
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "install");
        }

        send():JQueryPromise<api_rest.Response> {
            this.triggerElement.getHTMLElement().click();
            this.triggerElement.remove();
            return this.deferred;
        }

        private createUploader(triggerElement:api_dom.Element):any {
            if (!plupload) {
                throw new Error("ImageUploader: plupload not found, check if it is included in page.");
            }
            this.uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: false,
                browse_button: triggerElement.getId(),
                url: this.getRequestPath(),
                multipart: true,
                flash_swf_url: api_util.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api_util.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: [
                    {title: 'Zip Archive', extensions: 'zip'}
                ]
            });

            this.uploader.bind('Init', (up, params) => {
                // console.log('uploader init', up, params);
            });

            this.uploader.bind('FilesAdded', (up, files) => {
                // console.log('uploader files added', up, files);
            });

            this.uploader.bind('QueueChanged', (up) => {
                console.log('uploader queue changed', up);

                up.start();
            });

            this.uploader.bind('UploadFile', (up, file) => {
                console.log('uploader upload file', up, file);
            });

            this.uploader.bind('UploadProgress', (up, file) => {
                console.log('uploader upload progress', up, file);
            });

            this.uploader.bind('FileUploaded', (up, file, response) => {
                console.log('uploader file uploaded', up, file, response);

                if (response && response.status === 200) {
                    this.deferred.resolve(new api_rest.JsonResponse(response.response));
                } else {
                    this.deferred.reject(new api_rest.RequestError(response.statusText, response.responseText));
                }

            });

            this.uploader.bind('UploadComplete', (up, files) => {
                console.log('uploader upload complete', up, files);
            });

            this.uploader.bind('Error', (up, files) => {
                this.deferred.reject(new api_rest.RequestError(files.code, files.message));
            });

            this.uploader.init();

            return this.uploader;
        }

    }
}