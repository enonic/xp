declare var qq;

module api.ui.uploader {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import Element = api.dom.Element;

    export class PluploadStatus {
        public static QUEUED = plupload.QUEUED;
        public static UPLOADING = plupload.UPLOADING;
        public static FAILED = plupload.FAILED;
        public static DONE = plupload.DONE;
    }

    export interface PluploadFile {
        id: string;
        name: string;
        percent: number;
        type: string;
        size: number;
        origSize: number;
        loaded: number;
        status: PluploadStatus;
        lastModifiedDate: Date;
    }

    export interface FineUploaderFile {
        id: string,
        name: string,
        size: number,
        uuid: string,
        status: string,
        percent: number
    }

    export interface UploaderElConfig {
        name: string;
        url?: string;
        allowBrowse?: boolean;
        allowDrop?: boolean;
        dropAlwaysAllowed?: boolean;            // allow drop no matter if the dropzone is visible
        resultAlwaysVisisble?: boolean;         // never hide the result
        dropzoneAlwaysVisible?: boolean;       // never hide the dropzone
        allowTypes?: {title: string; extensions: string}[];
        allowMultiSelection?: boolean;
        showInput?: boolean;
        showReset?: boolean;
        showCancel?: boolean;
        showResult?: boolean;
        maximumOccurrences?: number;
        deferred?: boolean;
        params?: {[key:string]: any};
        value?: string;
        disabled?: boolean;
        hideDropZone?: boolean;
    }

    export class UploaderEl<MODEL extends api.Equitable> extends api.dom.FormInputEl {

        protected config: UploaderElConfig;
        protected uploader;
        protected value;
        private uploadedItems: UploadItem<MODEL>[] = [];

        private input: api.ui.text.TextInput;

        private dropzoneContainer: api.dom.DivEl;
        protected dropzone: api.dom.AEl;

        private progress: api.ui.ProgressBar;
        private cancelBtn: Button;

        private resultContainer: api.dom.DivEl;
        private resetBtn: CloseButton;

        private uploadStartedListeners: {(event: FileUploadStartedEvent<MODEL>):void }[] = [];
        private uploadProgressListeners: {(event: FileUploadProgressEvent<MODEL>):void }[] = [];
        private fileUploadedListeners: {(event: FileUploadedEvent<MODEL>):void }[] = [];
        private uploadCompleteListeners: { (event: FileUploadCompleteEvent<MODEL>):void }[] = [];
        private uploadFailedListeners: { (event: FileUploadFailedEvent<MODEL>):void }[] = [];
        private uploadResetListeners: {():void }[] = [];

        private debouncedUploadStart: () => void;

        public static debug: boolean = false;

        constructor(config: UploaderElConfig) {
            super("div", "uploader-el");

            // init defaults
            this.initConfig(config);
            this.initTextInput();

            if (this.config.value) {
                this.value = this.config.value;
            }

            this.initDropzone();

            this.appendChild(this.progress = new api.ui.ProgressBar());

            this.appendChild(this.resultContainer = new api.dom.DivEl('result-container'));

            this.initCancelButton();

            this.initResetButton();

            this.handleKeyEvents();

            this.handleInitTrigger();

            this.onRemoved((event) => this.destroyHandler.call(this, event));

            this.listenToDragEvents();
        }

        private initTextInput() {
            if (this.config.showInput) {
                this.input = api.ui.text.TextInput.middle();
                this.input.setPlaceholder("Paste URL to image here");
                this.appendChild(this.input);
            }
        }

        private initDropzone() {
            // need the container to constrain plupload created dropzone
            this.dropzoneContainer = new api.dom.DivEl('dropzone-container');
            this.dropzone = new api.dom.AEl("dropzone");
            // id needed for plupload to init, adding timestamp in case of multiple occurrences on page
            this.dropzone.setId('uploader-dropzone-' + new Date().getTime());
            this.dropzone.getEl().setTabIndex(-1);// for mac default settings
            this.getEl().setTabIndex(0);
            this.dropzoneContainer.appendChild(this.dropzone);
            if (this.config.hideDropZone) {
                this.dropzoneContainer.getEl().setAttribute("hidden", "true");
            }
            this.appendChild(this.dropzoneContainer);
        }

        private initCancelButton() {
            this.cancelBtn = new Button("Cancel");
            this.cancelBtn.setVisible(this.config.showCancel);
            this.cancelBtn.onClicked((event: MouseEvent) => {
                this.stop();
                this.reset();
            });
            this.appendChild(this.cancelBtn);
        }

        private initResetButton() {
            this.resetBtn = new CloseButton();
            this.resetBtn.setVisible(this.config.showReset);
            this.resetBtn.onClicked((event: MouseEvent) => {
                this.reset();
            });
            this.appendChild(this.resetBtn);
        }

        private handleKeyEvents() {
            this.onKeyPressed((event: KeyboardEvent) => {
                if (this.dropzoneContainer.isVisible() && event.keyCode == 13) {
                    wemjq(this.dropzone.getEl().getHTMLElement()).simulate("click");
                }
            });

            var resetHandler = () => {
                this.reset();
                return false;
            };
            KeyBindings.get().bindKeys([
                new KeyBinding('del', resetHandler),
                new KeyBinding('backspace', resetHandler)
            ]);
        }

        private handleInitTrigger() {
            if (this.config.deferred) {
                this.onShown((event) => this.initHandler.call(this, event));
            } else {
                this.onRendered((event) => this.initHandler.call(this, event));
            }
        }

        private listenToDragEvents() {
            var dragOverEl;
            // make use of the fact that when dragging first drag enter occurs on the child element and after that
            // drag leave occurs on the parent element that we came from meaning that to know when we left some element
            // we need to compare it to the one currently dragged over
            this.onDragEnter((event: DragEvent) => {
                if (this.config.dropAlwaysAllowed) {
                    var targetEl = <HTMLElement> event.target;

                    if (!dragOverEl) {

                        if (UploaderEl.debug) {
                            console.log('drag entered, adding class');
                        }
                        this.addClass('dragover');
                    }
                    dragOverEl = targetEl;
                }
            });

            this.onDragLeave((event: DragEvent) => {
                if (this.config.dropAlwaysAllowed) {
                    var targetEl = <HTMLElement> event.target;

                    if (dragOverEl == targetEl) {
                        if (UploaderEl.debug) {
                            console.log('drag left, removing class');
                        }
                        this.removeClass('dragover');
                        dragOverEl = undefined;
                    }
                }
            });

            this.onDrop((event: DragEvent) => {
                if (this.config.dropAlwaysAllowed) {
                    if (UploaderEl.debug) {
                        console.log('drag drop removing class');
                    }
                    this.removeClass('dragover');
                }
            });
        }

        protected setResetVisible(visible: boolean) {
            this.config.showReset = visible;
            this.resetBtn.setVisible(visible);
        }

        protected initHandler() {
            if (this.config.disabled) {
                if (UploaderEl.debug) {
                    console.log('Skipping init, because of config.disabled = true', this);
                }
            } else {
                if (UploaderEl.debug) {
                    console.log('Initing uploader', this);
                }
                if (!this.uploader && this.config.url) {
                    this.uploader = this.initUploader(
                        this.dropzone.getId(),
                        this.config.dropAlwaysAllowed ? this.getId() : this.dropzone.getId()
                    );

                    this.debouncedUploadStart = api.util.AppHelper.debounce(() => {
                        this.notifyFileUploadStarted(this.uploadedItems);
                        this.uploader.uploadStoredFiles();
                    }, 250, false);

                    if (this.value) {
                        this.setValue(this.value);
                    } else if (!this.config.hideDropZone) {
                        this.setDropzoneVisible();
                    }
                }
            }
        }

        private destroyHandler() {
            if (UploaderEl.debug) {
                console.log('Destroying uploader', this);
            }
            if (this.uploader) {
                this.uploader.reset(true);
                this.uploader = null;
            }
        }

        private initConfig(config: UploaderElConfig) {
            this.config = config;

            if (this.config.showResult == undefined) {
                this.config.showResult = true;
            }
            if (this.config.allowMultiSelection == undefined) {
                this.config.allowMultiSelection = false;
            }
            if (this.config.showCancel == undefined) {
                this.config.showCancel = true;
            }
            if (this.config.showReset == undefined) {
                this.config.showReset = true;
            }
            if (this.config.maximumOccurrences == undefined) {
                this.config.maximumOccurrences = 0;
            }
            if (this.config.allowBrowse == undefined) {
                this.config.allowBrowse = true;
            }
            if (this.config.allowDrop == undefined) {
                this.config.allowDrop = true;
            }
            if (this.config.dropAlwaysAllowed == undefined) {
                this.config.dropAlwaysAllowed = false;
            }
            if (this.config.dropzoneAlwaysVisible == undefined) {
                this.config.dropzoneAlwaysVisible = false;
            }
            if (this.config.resultAlwaysVisisble == undefined) {
                this.config.resultAlwaysVisisble = false;
            }
            if (this.config.allowTypes == undefined) {
                this.config.allowTypes = [];
            }
            if (this.config.deferred == undefined) {
                this.config.deferred = false;
            }
            if (this.config.disabled == undefined) {
                this.config.disabled = false;
            }
        }

        getName(): string {
            return this.config.name;
        }

        doGetValue(): string {
            return this.value;
        }

        doSetValue(value: string, silent?: boolean): UploaderEl<MODEL> {
            if (UploaderEl.debug) {
                console.log('Setting uploader value', value, this);
            }
            this.value = value;

            if (value) {
                if (this.config.showResult) {
                    this.setResultVisible();
                } else {
                    this.setDropzoneVisible();
                }
            } else {
                this.setDropzoneVisible();
                this.getResultContainer().removeChildren();
                return this;
            }

            var newItemsToAppend: Element[] = [],
                existingItems: Element[] = [];

            this.parseValues(value).forEach((val) => {
                if (val) {
                    var existingItem = this.getExistingItem(val);
                    if (!existingItem) {
                        newItemsToAppend.push(this.createResultItem(val));
                    } else {
                        this.refreshExistingItem(existingItem, val);
                        existingItems.push(existingItem);
                    }
                }
            });

            this.removeAllChildrenExceptGiven(existingItems);
            this.appendNewItems(newItemsToAppend);

            return this;
        }

        protected appendNewItems(newItemsToAppend: Element[]) {
            for (var key in newItemsToAppend) {
                this.getResultContainer().appendChild(newItemsToAppend[key]);
            }
        }

        protected removeAllChildrenExceptGiven(itemsToKeep: Element[]) {
            var items = this.getResultContainer().getChildren(),
                toRemove = [];

            items.forEach((elem) => {
                if (!itemsToKeep.some((itemToKeep) => itemToKeep == elem)) {
                    toRemove.push(elem);
                }
            });
            for (var key in toRemove) {
                toRemove[key].remove();
            }
        }

        protected refreshExistingItem(existingItem: Element, value: string) {

        }

        protected getExistingItem(value: string): Element {
            return null;
        }

        parseValues(jsonString: string): string[] {
            try {
                var o = JSON.parse(jsonString);

                // Handle non-exception-throwing cases:
                // Neither JSON.parse(false) or JSON.parse(1234) throw errors, hence the type-checking,
                // but... JSON.parse(null) returns 'null', and typeof null === "object",
                if (o && typeof o === "object" && o.length) {
                    return o;
                }
            } catch (e) { }

            // Value is not JSON so just return it
            return [jsonString];
        }

        createResultItem(value: string): api.dom.Element {
            throw new Error('Should be overridden by inheritors');
        }

        setMaximumOccurrences(value: number): UploaderEl<MODEL> {
            this.config.maximumOccurrences = value;
            return this;
        }

        stop(): UploaderEl<MODEL> {
            if (this.uploader) {
                this.uploader.stop();
            }
            return this;
        }

        reset(): UploaderEl<MODEL> {
            this.setValue(null);
            this.notifyUploadReset();
            return this;
        }

        protected getErrorMessage(fileString: string): string {
            return "File(s) [" + fileString + "] were not uploaded";
        }

        protected setDropzoneVisible(visible: boolean = true) {
            if (!visible && this.config.dropzoneAlwaysVisible) {
                return;
            }

            if (visible) {
                this.setProgressVisible(false);
                this.setResultVisible(false);
            }

            if (this.input) {
                this.input.setVisible(visible);
            }
            this.dropzoneContainer.setVisible(visible);
        }

        setProgressVisible(visible: boolean = true) {
            if (visible) {
                this.setDropzoneVisible(false);
                this.setResultVisible(false);
            }

            this.progress.setVisible(visible);
            this.cancelBtn.setVisible(visible && this.config.showCancel);
        }

        setResultVisible(visible: boolean = true) {
            if (!visible && this.config.resultAlwaysVisisble) {
                return;
            }

            if (visible) {
                this.setDropzoneVisible(false);
                this.setProgressVisible(false);
            }

            this.resultContainer.setVisible(visible);
            this.resetBtn.setVisible(visible && this.config.showReset);
        }


        createModel(serverResponse): MODEL {
            throw new Error('Should be overridden by inheritors');
        }


        getModelValue(item: MODEL): string {
            throw new Error('Should be overridden by inheritors');
        }

        setParams(params: {[key: string]: any}): UploaderEl<MODEL> {
            if (this.uploader) {
                this.uploader.setParams(params);
            }

            this.config.params = params;

            return this;
        }

        setEnabled(enabled: boolean): UploaderEl<MODEL> {
            this.config.disabled = !enabled;

            if (!enabled) {
                this.dropzone.getEl().setAttribute('disabled', 'true');
            } else {
                this.dropzone.getEl().removeAttribute('disabled');
            }

            if (!enabled && this.uploader) {
                if (UploaderEl.debug) {
                    console.log('Disabling uploader', this);
                }
                this.destroyHandler();

            } else if (enabled && !this.uploader) {

                if (UploaderEl.debug) {
                    console.log('Enabling uploader', this);
                }
                if (this.config.deferred) {

                    if (this.isVisible()) {
                        this.initHandler();
                    } else {
                        if (UploaderEl.debug) {
                            console.log('Deferring enabling uploader until it\' shown', this);
                        }
                        this.onShown((event) => this.initHandler.call(this, event));
                    }
                } else {

                    if (this.isRendered()) {
                        this.initHandler();
                    } else {
                        if (UploaderEl.debug) {
                            console.log('Deferring enabling uploader until it\' rendered', this);
                        }
                        this.onRendered((event) => this.initHandler.call(this, event));
                    }
                }
            }
            return this;
        }

        isEnabled(): boolean {
            return this.uploader != null;
        }

        getParams(): {[key: string]: any} {
            return this.config.params;
        }

        private findUploadItemById(id: string): UploadItem<MODEL> {
            for (var i = 0; i < this.uploadedItems.length; i++) {
                var uploadItem = this.uploadedItems[i];
                if (uploadItem.getId() == id) {
                    return uploadItem;
                }
            }
            return null;
        }

        private isFolder(file: PluploadFile): boolean {
            // there's no way to detect if uploaded file is a folder so this is as close as we can get
            return file.size % 4096 == 0 && api.util.StringHelper.isEmpty(file.type);
        }

        private validateFiles(up, files: PluploadFile[]) {
            // Check for folders
            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                if (this.isFolder(file)) {
                    if (UploaderEl.debug) {
                        console.log('Removing folder [' + file.name + '] because it can\'t be uploaded', this);
                    }

                    files.splice(i, 1);
                    up.splice(i, 1);
                    api.notify.NotifyManager.get().showWarning('Folder [' + file.name + '] upload is not supported yet');
                }
            }

            // Check for max allowed occurrences
            /* if (this.config.maximumOccurrences > 0 && files.length > this.config.maximumOccurrences) {
             if (UploaderEl.debug) {
             console.log('Max ' + this.config.maximumOccurrences + ' files allowed, removing the rest', this);
             }

             files.splice(this.config.maximumOccurrences);
             up.splice(this.config.maximumOccurrences);
             api.notify.NotifyManager.get().showWarning('Max ' + this.config.maximumOccurrences + ' files are allowed');
             }*/

            return files.length > 0;
        }

        private submitCallback(id, name) {
            var file: FineUploaderFile = this.uploader.getFile(id);
            file.id = id;

            var uploadFile = new UploadItem<MODEL>(file);
            this.uploadedItems.push(uploadFile);

            this.setProgressVisible();

            this.debouncedUploadStart();
        }

        private statusChangeCallback(id, oldStatus, newStatus) {
            var uploadItem = this.findUploadItemById(id);
            if (!!uploadItem) {
                uploadItem.setStatus(newStatus);
            }
        }

        private progressCallback(id, name, uploadedBytes, totalBytes) {
            var percent = Math.round(uploadedBytes / totalBytes * 100);

            this.progress.setValue(percent);

            var uploadItem = this.findUploadItemById(id);
            if (uploadItem) {
                uploadItem.setProgress(percent);
                this.notifyFileUploadProgress(uploadItem);
            }
        }

        private fileCompleteCallback(id, name, response, xhrOrXdr) {
            if (xhrOrXdr && xhrOrXdr.status === 200) {
                try {
                    var uploadItem = this.findUploadItemById(id);
                    if (uploadItem) {
                        var model: MODEL = this.createModel(JSON.parse(xhrOrXdr.response));
                        uploadItem.setModel(model);
                        this.notifyFileUploaded(uploadItem);
                    }
                } catch (e) {
                    console.warn("Failed to parse the response", response, e);
                }
            }
        }

        private errorCallback(id, name, errorReason, xhrOrXdr) {
            if (xhrOrXdr && xhrOrXdr.status !== 200) {
                try {
                    var responseObj = JSON.parse(xhrOrXdr.response);
                    var error = new api.rest.RequestError(responseObj.status, responseObj.message);
                    api.DefaultErrorHandler.handle(error);
                } catch (e) {
                    console.warn("Failed to parse the response", xhrOrXdr.response, e);
                    api.notify.NotifyManager.get().showError(this.getErrorMessage(name));
                }

                var uploadItem = this.findUploadItemById(id);
                if (uploadItem) {
                    uploadItem.setModel(null);
                    this.notifyUploadFailed(uploadItem);
                }
            }
        }

        private allCompleteCallback(succeeded, failed) {
            var values = [];
            this.uploadedItems.forEach((item) => {
                if (item.getStatus() == qq.status.UPLOAD_SUCCESSFUL) {
                    if (item.getModel()) {
                        values.push(this.getModelValue(item.getModel()));
                    } else {
                        item.notifyFailed();
                        this.notifyUploadFailed(item);
                    }
                }
            });

            if (values.length > 0) {
                this.setValue(JSON.stringify(values), false, true);
                this.notifyUploadCompleted(this.uploadedItems);
            }

            this.uploadedItems.length = 0;
        }

        protected initUploader(browseId: string, dropId: string) {
            var uploader = new qq.FineUploaderBasic({
                debug: false,
                button: this.config.allowBrowse ? document.getElementById(browseId) : undefined,
                multiple: this.config.allowMultiSelection,
                folders: false,
                autoUpload: false,
                request: {
                    endpoint: this.config.url,
                    params: this.config.params,
                    inputName: "file",
                    filenameParam: "name"
                },
                validation: {
                    acceptFiles: this.config.allowTypes
                },
                callbacks: {
                    onSubmit: this.submitCallback.bind(this),
                    onStatusChange: this.statusChangeCallback.bind(this),
                    onProgress: this.progressCallback.bind(this),
                    onComplete: this.fileCompleteCallback.bind(this),
                    onError: this.errorCallback.bind(this),
                    onAllComplete: this.allCompleteCallback.bind(this)
                }
            });

            if (this.config.allowDrop) {
                new qq.DragAndDrop({
                    dropZoneElements: [document.getElementById(dropId)],
                    callbacks: {
                        processingDroppedFilesComplete: function (files, dropTarget) {
                            uploader.addFiles(files); //this submits the dropped files to Fine Uploader
                        }
                    }
                });
            }

            this.disableInputFocus(); // on init
            return uploader;
        }

        private disableInputFocus() {
            var focusableElements: NodeListOf<HTMLInputElement> = this.getDropzoneContainer().getHTMLElement().getElementsByTagName("input");
            for (var i = 0; i < focusableElements.length; i++) {
                var el = <HTMLInputElement>focusableElements.item(i);
                el.tabIndex = -1;
            }
        }

        getResultContainer(): api.dom.DivEl {
            return this.resultContainer;
        }

        getDropzoneContainer(): api.dom.DivEl {
            return this.dropzoneContainer;
        }

        getDropzone(): api.dom.AEl {
            return this.dropzone;
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent<MODEL>) => void) {
            this.uploadStartedListeners.push(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent<MODEL>) => void) {
            this.uploadStartedListeners = this.uploadStartedListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        onUploadProgress(listener: (event: FileUploadProgressEvent<MODEL>) => void) {
            this.uploadProgressListeners.push(listener);
        }

        unUploadProgress(listener: (event: FileUploadProgressEvent<MODEL>) => void) {
            this.uploadProgressListeners = this.uploadProgressListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        onFileUploaded(listener: (event: FileUploadedEvent<MODEL>) => void) {
            this.fileUploadedListeners.push(listener);
        }

        unFileUploaded(listener: (event: FileUploadedEvent<MODEL>) => void) {
            this.fileUploadedListeners = this.fileUploadedListeners.filter((currentListener) => {
                return listener != currentListener;
            })
        }

        onUploadCompleted(listener: (event: FileUploadCompleteEvent<MODEL>) => void) {
            this.uploadCompleteListeners.push(listener);
        }

        unUploadCompleted(listener: (event: FileUploadCompleteEvent<MODEL>) => void) {
            this.uploadCompleteListeners = this.uploadCompleteListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        onUploadReset(listener: () => void) {
            this.uploadResetListeners.push(listener);
        }

        unUploadReset(listener: () => void) {
            this.uploadResetListeners = this.uploadResetListeners.filter((currentListener) => {
                return listener != currentListener;
            })
        }

        onUploadFailed(listener: (event: FileUploadFailedEvent<MODEL>) => void) {
            this.uploadFailedListeners.push(listener);
        }

        unUploadFailed(listener: (event: FileUploadFailedEvent<MODEL>) => void) {
            this.uploadFailedListeners = this.uploadFailedListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        private notifyFileUploadStarted(uploadItems: UploadItem<MODEL>[]) {
            this.uploadStartedListeners.forEach((listener: (event: FileUploadStartedEvent<MODEL>)=>void) => {
                listener(new FileUploadStartedEvent<MODEL>(uploadItems));
            });
        }

        private notifyFileUploadProgress(uploadItem: UploadItem<MODEL>) {
            this.uploadProgressListeners.forEach((listener: (event: FileUploadProgressEvent<MODEL>)=>void) => {
                listener(new FileUploadProgressEvent<MODEL>(uploadItem));
            });
        }

        private notifyFileUploaded(uploadItem: UploadItem<MODEL>) {
            this.fileUploadedListeners.forEach((listener: (event: FileUploadedEvent<MODEL>)=>void) => {
                listener.call(this, new FileUploadedEvent<MODEL>(uploadItem));
            });
        }

        private notifyUploadCompleted(uploadItems: UploadItem<MODEL>[]) {
            this.uploadCompleteListeners.forEach((listener: (event: FileUploadCompleteEvent<MODEL>)=>void) => {
                listener(new FileUploadCompleteEvent<MODEL>(uploadItems));
            });
        }

        private notifyUploadReset() {
            this.uploadResetListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        private notifyUploadFailed(uploadItem: UploadItem<MODEL>) {
            this.uploadFailedListeners.forEach((listener: (event: FileUploadFailedEvent<MODEL>)=>void) => {
                listener(new FileUploadFailedEvent<MODEL>(uploadItem));
            });
        }
    }
}