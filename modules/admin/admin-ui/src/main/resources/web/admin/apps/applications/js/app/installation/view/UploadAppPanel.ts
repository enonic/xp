module app.installation.view {

    import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
    import InputEl = api.dom.InputEl;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import Action = api.ui.Action;

    export class UploadAppPanel extends api.ui.panel.Panel {

        private applicationInput: ApplicationInput;

        private applicationUploaderEl: api.application.ApplicationUploaderEl;

        constructor(cancelAction: Action, className?: string) {
            super(className);

            this.initApplicationInput(cancelAction);

            this.initApplicationUploader();
        }

        getApplicationInput(): ApplicationInput {
            return this.applicationInput;
        }

        getApplicationUploaderEl(): ApplicationUploaderEl {
            return this.applicationUploaderEl;
        }

        private initApplicationInput(cancelAction: Action) {
            this.applicationInput = new ApplicationInput(cancelAction, 'large').
                setPlaceholder("Paste link or drop files here");

            this.appendChild(this.applicationInput);
        }

        private initApplicationUploader() {

            var uploaderContainer = new api.dom.DivEl('uploader-container');
            this.appendChild(uploaderContainer);

            var uploaderMask = new api.dom.DivEl('uploader-mask');
            uploaderContainer.appendChild(uploaderMask);

            this.applicationUploaderEl = new api.application.ApplicationUploaderEl({
                params: {},
                name: 'application-uploader',
                showResult: false,
                allowMultiSelection: false,
                deferred: true  // wait till the window is shown
            });
            uploaderContainer.appendChild(this.applicationUploaderEl);

            var dragOverEl;
            // make use of the fact that when dragging
            // first drag enter occurs on the child element and after that
            // drag leave occurs on the parent element that we came from
            // meaning that to know when we left some element
            // we need to compare it to the one currently dragged over
            this.onDragEnter((event: DragEvent) => {
                var target = <HTMLElement> event.target;

                if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                    uploaderContainer.show();
                }
                dragOverEl = target;
            });

            this.onDragLeave((event: DragEvent) => {
                var targetEl = <HTMLElement> event.target;

                if (dragOverEl == targetEl) {
                    uploaderContainer.hide();
                }
            });

            this.onDrop((event: DragEvent) => {
                uploaderContainer.hide();
            });
        }
    }

    export class ApplicationInput extends api.dom.CompositeFormInputEl {

        private textInput: InputEl;
        private applicationUploaderEl: ApplicationUploaderEl;
        private lastTimeKeyPressedTimer;
        private LAST_KEY_PRESS_TIMEOUT: number;
        private mask: api.ui.mask.LoadMask;
        private cancelAction: Action;

        constructor(cancelAction: Action, className?: string, originalValue?: string) {
            this.textInput = new InputEl("text");
            this.LAST_KEY_PRESS_TIMEOUT = 1500;
            this.cancelAction = cancelAction;

            this.applicationUploaderEl = new ApplicationUploaderEl({
                name: 'application-input-uploader',
                allowDrop: false,
                showResult: false,
                allowMultiSelection: true,
                deferred: true,  // wait till it's shown
                value: originalValue
            });

            this.applicationUploaderEl.onUploadStarted((event: api.ui.uploader.FileUploadStartedEvent<Application>) => {
                var names = event.getUploadItems().map((uploadItem: api.ui.uploader.UploadItem<Application>) => {
                    return uploadItem.getName();
                });
                this.textInput.setValue(names.join(', '));
            });

            super(this.textInput, this.applicationUploaderEl);
            this.addClass("file-input" + (className ? " " + className : ""));
            this.initUrlEnteredHandler();
        }

        private initUrlEnteredHandler() {
            this.onKeyDown((event) => {
                clearTimeout(this.lastTimeKeyPressedTimer);

                if (event.keyCode !== 27) {
                    this.lastTimeKeyPressedTimer = setTimeout(() => {
                        if (!api.util.StringHelper.isEmpty(this.textInput.getValue())) {
                            this.initMask();
                            this.mask.show();
                            this.installWithUrl(this.textInput.getValue());
                            console.log("url: " + this.textInput.getValue());
                        }
                    }, this.LAST_KEY_PRESS_TIMEOUT);
                }
            });
        }

        private initMask() {
            if (!this.mask) {
                this.mask = new api.ui.mask.LoadMask(this);
                this.getParentElement().appendChild(this.mask);
            }
        }

        private installWithUrl(url: string) {
            new api.application.InstallUrlApplicationRequest(url).sendAndParse().then((application: api.application.Application)=> {
                this.mask.hide();
                this.cancelAction.execute();
            }).catch((reason: any) => {
                this.mask.hide();
                api.DefaultErrorHandler.handle(reason);
            });
        }

        setUploaderParams(params: {[key: string]: any}): ApplicationInput {
            this.applicationUploaderEl.setParams(params);
            return this;
        }

        getUploaderParams(): {[key: string]: string} {
            return this.applicationUploaderEl.getParams();
        }

        setPlaceholder(placeholder: string): ApplicationInput {
            this.textInput.setPlaceholder(placeholder);
            return this;
        }

        getPlaceholder(): string {
            return this.textInput.getPlaceholder();
        }

        reset(): ApplicationInput {
            this.textInput.reset();
            this.applicationUploaderEl.reset();
            return this;
        }

        stop(): ApplicationInput {
            this.applicationUploaderEl.stop();
            return this;
        }

        getUploader(): ApplicationUploaderEl {
            return this.applicationUploaderEl;
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent<Application>) => void) {
            this.applicationUploaderEl.onUploadStarted(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent<Application>) => void) {
            this.applicationUploaderEl.unUploadStarted(listener);
        }
    }

}