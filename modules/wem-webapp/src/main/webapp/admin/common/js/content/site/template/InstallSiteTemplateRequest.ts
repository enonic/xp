module api.content.site.template {

    export class InstallSiteTemplateRequest extends SiteTemplateResourceRequest<api.content.site.template.json.SiteTemplateSummaryJson> {

        private uploader: any;
        private triggerElement: api.dom.Element;
        private isExternalTriggerElement: boolean = false;

        private multiSelection: boolean = true;

        private deferred: Q.Deferred<InstallSiteTemplateResponse>;

        private doneCallback: (value: InstallSiteTemplateResponse) => void;
        private failCallback: (reason: api.rest.RequestError) => void;

        constructor(triggerEl?: api.dom.Element) {
            super();
            if (triggerEl) {
                this.triggerElement = triggerEl;
                this.isExternalTriggerElement = true;
            } else {
                this.triggerElement = new api.dom.ButtonEl();
                this.triggerElement.hide();
                api.dom.Body.get().appendChild(this.triggerElement);
            }

            this.uploader = this.createUploader(this.triggerElement);
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "import");
        }

        setMultiSelection(value: boolean): InstallSiteTemplateRequest {
            this.multiSelection = value;
            return this;
        }

        send(): Q.Promise<InstallSiteTemplateResponse> {
            this.deferred = Q.defer<InstallSiteTemplateResponse>();
            if (this.doneCallback) {
                this.deferred.promise.then(this.doneCallback);
            }
            if (this.failCallback) {
                this.deferred.promise.catch(this.failCallback);
            }
            if (!this.isExternalTriggerElement) {
                this.triggerElement.getHTMLElement().click();
                this.triggerElement.remove();
            }
            this.uploader.start();
            return this.deferred.promise;
        }

        done(fn: (resp: InstallSiteTemplateResponse)=>void) {
            if (this.deferred) {
                this.deferred.promise.then(fn);
            }
            this.doneCallback = fn;
        }

        fail(fn: (resp: api.rest.RequestError)=>void) {
            if (this.deferred) {
                this.deferred.promise.catch(fn);
            }
            this.failCallback = fn;
        }

        stop() {
            this.uploader.stop();
        }

        private createUploader(triggerElement: api.dom.Element): any {
            if (!plupload) {
                throw new Error("InstallSiteTemplateRequest: plupload not found, check if it is included in page.");
            }

            this.uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: this.multiSelection,
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

            var results: api.rest.JsonResponse<api.content.site.template.json.SiteTemplateSummaryJson>[] = [];
            this.uploader.bind('QueueChanged', (up) => {
                this.send();
                results.length = 0;
            });

            this.uploader.bind('FileUploaded', (up, file, response) => {
                if (response && response.status === 200) {
                    results.push(new api.rest.JsonResponse<api.content.site.template.json.SiteTemplateSummaryJson>(response.response));
                } else if (this.deferred) {
                    this.deferred.reject(new api.rest.RequestError(response.status, response.statusText, response.responseText, null));
                    this.deferred = undefined;
                }

            });

            this.uploader.bind('UploadComplete', (up, files) => {
                if (this.deferred) {
                    this.deferred.resolve(new InstallSiteTemplateResponse(results));
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

    export class InstallSiteTemplateResponse extends api.rest.Response {

        private templates: SiteTemplateSummary[] = [];

        constructor(templateResponses: api.rest.JsonResponse<api.content.site.template.json.SiteTemplateSummaryJson>[]) {
            super();
            templateResponses.forEach((response: api.rest.JsonResponse<api.content.site.template.json.SiteTemplateSummaryJson>) => {
                var responseJson = response.getJson();
                if (responseJson) {
                    this.templates.push(new SiteTemplateSummary(responseJson));
                }
            });
        }

        getSiteTemplates(): SiteTemplateSummary[] {
            return this.templates;
        }

    }
}