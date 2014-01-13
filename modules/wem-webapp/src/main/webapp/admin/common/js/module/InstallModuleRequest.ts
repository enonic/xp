module api.module {

    export class InstallModuleRequest extends ModuleResourceRequest<api.module.json.ModuleSummaryJson> {

        private uploader:any;
        private triggerElement:api.dom.Element;
        private isExternalTriggerElement:boolean = false;

        private deferred:JQueryDeferred<api.rest.Response>;

        private doneCallback: (response: InstallModuleResponse) => void;
        private failCallback: (response: api.rest.Response) => void;

        constructor(triggerEl?:api.dom.Element) {
            super();
            if (triggerEl) {
                this.triggerElement = triggerEl;
                this.isExternalTriggerElement = true;
            } else {
                this.triggerElement = new api.dom.ButtonEl(true);
                this.triggerElement.hide();
                api.dom.Body.get().appendChild(this.triggerElement);
            }

            this.uploader = this.createUploader(this.triggerElement);
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "install");
        }

        send():JQueryPromise<api.rest.Response> {
            this.deferred = jQuery.Deferred<api.rest.Response>();
            if (this.doneCallback) {
                this.deferred.done(this.doneCallback);
            }
            if (this.failCallback) {
                this.deferred.fail(this.failCallback);
            }
            if (!this.isExternalTriggerElement) {
                this.triggerElement.getHTMLElement().click();
                this.triggerElement.remove();
            }
            this.uploader.start();
            return this.deferred;
        }

        done(fn:(resp:InstallModuleResponse)=>void) {
            if (this.deferred) {
                this.deferred.done(fn);
            }
            this.doneCallback = fn;
        }

        fail(fn:(resp:api.rest.Response)=>void) {
            if (this.deferred) {
                this.deferred.fail(fn);
            }
            this.failCallback = fn;
        }

        stop() {
            this.uploader.stop();
        }

        private createUploader(triggerElement:api.dom.Element):any {
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
                flash_swf_url: api.util.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api.util.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: [
                    {title: 'Zip Archive', extensions: 'zip'}
                ]
            });

            var results:api.rest.JsonResponse<api.module.json.ModuleSummaryJson>[] = [];
            this.uploader.bind('QueueChanged', (up) => {
                this.send();
                results.length = 0;
            });

            this.uploader.bind('FileUploaded', (up, file, response) => {
                if (response && response.status === 200) {
                    results.push(new api.rest.JsonResponse<api.module.json.ModuleSummaryJson>(response.response));
                } else {
                    this.deferred.reject(new api.rest.RequestError(response.status, response.statusText, response.responseText, null));
                    this.deferred = undefined;
                }
            });

            this.uploader.bind('UploadComplete', (up, files) => {
                if (this.deferred) {
                    this.deferred.resolve(new InstallModuleResponse(results));
                    this.deferred = undefined;
                }
            });

            this.uploader.bind('Error', (up, files) => {
                if (this.deferred) {
                    this.deferred.reject(new api.rest.RequestError(null, files.code, files.message, null));
                    this.deferred = undefined;
                }
            });

            this.uploader.init();

            return this.uploader;
        }

    }

    export class InstallModuleResponse extends api.rest.Response {

        private modules:ModuleSummary[] = [];

        constructor (moduleResponses:api.rest.JsonResponse<api.module.json.ModuleSummaryJson>[]) {
            super();
            moduleResponses.forEach((response:api.rest.JsonResponse<api.module.json.ModuleSummaryJson>) => {
                var responseJson = response.getJson();
                if (responseJson) {
                    this.modules.push(new ModuleSummary(responseJson));
                }
            });
        }

        getModules():ModuleSummary[] {
            return this.modules;
        }
    }
}