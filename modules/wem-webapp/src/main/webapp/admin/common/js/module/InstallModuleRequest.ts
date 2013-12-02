module api_module {

    export class InstallModuleRequest extends ModuleResourceRequest<any> {

        private uploader:any;
        private triggerElement:api_dom.Element;
        private isExternalTriggerElement:boolean = false;

        private deferred:JQueryDeferred<api_rest.Response>;

        constructor(triggerEl?:api_dom.Element) {
            super();
            if (triggerEl) {
                this.triggerElement = triggerEl;
                this.isExternalTriggerElement = true;
            } else {
                this.triggerElement = new api_dom.ButtonEl("trigger-el");
                this.triggerElement.hide();
                api_dom.Body.get().appendChild(this.triggerElement);
            }
            this.deferred = jQuery.Deferred<api_rest.Response>();

            this.uploader = this.createUploader(this.triggerElement);
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "install");
        }

        send():JQueryPromise<api_rest.Response> {
            if (!this.isExternalTriggerElement) {
                this.triggerElement.getHTMLElement().click();
                this.triggerElement.remove();
            }
            return this.deferred;
        }

        promise():JQueryPromise<api_rest.Response> {
            return this.deferred;
        }

        private createUploader(triggerElement:api_dom.Element):any {
            if (!plupload) {
                throw new Error("ImageUploader: plupload not found, check if it is included in page.");
            }
            this.uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: true,
                browse_button: triggerElement.getId(),
                url: this.getRequestPath(),
                multipart: true,
                drop_element: triggerElement.getId(),
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

            var results:any = [];
            this.uploader.bind('FileUploaded', (up, file, response) => {
                console.log('uploader file uploaded', up, file, response);

                if (response && response.status === 200) {
                    results.push(new api_rest.JsonResponse(response.response));
                    //this.deferred.resolve(new api_rest.JsonResponse(response.response));
                } else {
                    this.deferred.reject(new api_rest.RequestError(response.statusText, response.responseText));
                }

            });

            this.uploader.bind('UploadComplete', (up, files) => {
                console.log('uploader upload complete', up, files);
                this.deferred.resolve(new InstallModuleResponse(results));
            });

            this.uploader.bind('Error', (up, files) => {
                this.deferred.reject(new api_rest.RequestError(files.code, files.message));
            });

            this.uploader.init();

            return this.uploader;
        }

    }

    export class InstallModuleResponse extends api_rest.JsonResponse<any> {

        private moduleResponses:api_rest.JsonResponse<api_module.Module>[];

        constructor (moduleResponses:api_rest.JsonResponse<api_module.Module>[]) {
            super( '{}' );
            this.moduleResponses = moduleResponses
        }

        getModules():Module[]{
            return this.moduleResponses.map((resp:api_rest.JsonResponse) => { return new Module(resp.getJson().result)})
        }
    }
}