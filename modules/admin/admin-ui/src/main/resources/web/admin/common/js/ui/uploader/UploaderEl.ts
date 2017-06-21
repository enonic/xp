declare var qq;

module api.ui.uploader {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import Element = api.dom.Element;
    import ElementRenderedEvent = api.dom.ElementRenderedEvent;
    import ElementShownEvent = api.dom.ElementShownEvent;
    import i18n = api.util.i18n;

    export interface FineUploaderFile {
        id: number;
        name: string;
        size: number;
        uuid: string;
        status: string;
        percent: number;
    }

    export interface UploaderElConfig {
        name: string;
        url?: string;
        hasUploadButton?: boolean;
        allowDrop?: boolean;
        selfIsDropzone?: boolean;       // allow drop no matter if the dropzone is visible
        resultAlwaysVisisble?: boolean; // never hide the result
        allowTypes?: {title: string; extensions: string}[];
        allowMultiSelection?: boolean;
        showCancel?: boolean;
        showResult?: boolean;
        maximumOccurrences?: number;
        deferred?: boolean;
        params?: {[key: string]: any};
        value?: string;
        disabled?: boolean;
        hideDefaultDropZone?: boolean;
    }

    export class UploaderEl<MODEL extends api.Equitable> extends api.dom.FormInputEl {

        protected config: UploaderElConfig;
        protected uploader: any;       // qq.FineUploaderBasic
        protected dragAndDropper: any; // qq.DragAndDrop
        protected value: string;
        private uploadedItems: UploadItem<MODEL>[] = [];
        private extraDropzoneIds: string[] = [];

        private defaultDropzoneContainer: DropzoneContainer;
        protected dropzone: api.dom.AEl;
        private uploadButton: api.dom.DivEl;

        private progress: api.ui.ProgressBar;
        private cancelBtn: Button;

        private resultContainer: api.dom.DivEl;

        private uploadStartedListeners: {(event: FileUploadStartedEvent<MODEL>): void }[] = [];
        private uploadProgressListeners: {(event: FileUploadProgressEvent<MODEL>): void }[] = [];
        private fileUploadedListeners: {(event: FileUploadedEvent<MODEL>): void }[] = [];
        private uploadCompleteListeners: { (event: FileUploadCompleteEvent<MODEL>): void }[] = [];
        private uploadFailedListeners: { (event: FileUploadFailedEvent<MODEL>): void }[] = [];
        private uploadResetListeners: {(): void }[] = [];
        private dropzoneDragEnterListeners: { (event: DragEvent): void }[] = [];
        private dropzoneDragLeaveListeners: { (event: DragEvent): void }[] = [];
        private dropzoneDropListeners: { (event: DragEvent): void }[] = [];

        private debouncedUploadStart: () => void;

        private shownInitHandler: (event: ElementShownEvent) => void;
        private renderedInitHandler: (event: ElementRenderedEvent) => void;

        public static debug: boolean = false;

        constructor(config: UploaderElConfig) {
            super('div', 'uploader-el');

            // init defaults
            this.initConfig(config);

            if (this.config.value) {
                this.value = this.config.value;
            }

            this.initUploadButton();

            this.initDropzone();

            this.appendChild(this.progress = new api.ui.ProgressBar());

            this.appendChild(this.resultContainer = new api.dom.DivEl('result-container'));

            this.initCancelButton();

            this.handleKeyEvents();

            this.initDebouncedUploadStart();

            let initHandlerOnEvent = (event) => {
                this.initHandler();

                if (this.config.deferred) {
                    this.unShown(initHandlerOnEvent);
                } else {
                    this.unRendered(initHandlerOnEvent);
                }
            };

            if (this.config.deferred) {
                this.onShown(initHandlerOnEvent);
            } else {
                this.onRendered(initHandlerOnEvent);
            }

            this.onRemoved((event) => this.destroyHandler());
        }

        private initUploadButton() {
            if (!this.config.hasUploadButton) {
                return;
            }
            this.uploadButton = new api.dom.DivEl('upload-button');
            this.uploadButton.setId('upload-button-' + new Date().getTime());
            this.uploadButton.onClicked((event: MouseEvent) => {
                this.showFileSelectionDialog();
            });
            this.appendChild(this.uploadButton);
        }

        private initDebouncedUploadStart() {
            this.debouncedUploadStart = api.util.AppHelper.debounce(() => {
                this.notifyFileUploadStarted(this.uploadedItems);
                this.uploader.uploadStoredFiles();
            }, 250, false);
        }

        private initDropzone() {
            this.defaultDropzoneContainer = new DropzoneContainer();
            this.dropzone = this.defaultDropzoneContainer.getDropzone();
            this.defaultDropzoneContainer.addClass('default-dropzone-container');
            this.appendChild(this.defaultDropzoneContainer);
        }

        private initCancelButton() {
            this.cancelBtn = new Button('Cancel');
            this.cancelBtn.setVisible(this.config.showCancel);
            this.cancelBtn.onClicked((event: MouseEvent) => {
                this.stop();
                this.reset();
            });
            this.appendChild(this.cancelBtn);
        }

        private handleKeyEvents() {
            this.onKeyPressed((event: KeyboardEvent) => {
                if (this.defaultDropzoneContainer.isVisible() && event.keyCode === 13) {
                    wemjq(this.dropzone.getEl().getHTMLElement()).simulate('click');
                }
            });

            let resetHandler = () => {
                this.reset();
                return false;
            };
            KeyBindings.get().bindKeys([
                new KeyBinding('del', resetHandler),
                new KeyBinding('backspace', resetHandler)
            ]);
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
                    this.uploader = this.initUploader();

                    if (this.value) {
                        this.setValue(this.value);
                    } else if (!this.config.hideDefaultDropZone) {
                        this.setDefaultDropzoneVisible();
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
            if (this.dragAndDropper) {
                this.dragAndDropper.dispose();
                this.dragAndDropper = null;
            }
        }

        private initConfig(config: UploaderElConfig) {
            this.config = config;

            if (this.config.showResult == null) {
                this.config.showResult = true;
            }
            if (this.config.allowMultiSelection == null) {
                this.config.allowMultiSelection = false;
            }
            if (this.config.showCancel == null) {
                this.config.showCancel = true;
            }

            //TODO: property is not used. it might have sense to use it when filtering upload file candidates.
            // otherwise - just remove it
            if (this.config.maximumOccurrences == null) {
                this.config.maximumOccurrences = 0;
            }
            if (this.config.hasUploadButton == null) {
                this.config.hasUploadButton = true;
            }
            if (this.config.allowDrop == null) {
                this.config.allowDrop = true;
            }
            if (this.config.selfIsDropzone == null) {
                this.config.selfIsDropzone = false;
            }
            if (this.config.resultAlwaysVisisble == null) {
                this.config.resultAlwaysVisisble = false;
            }
            if (this.config.allowTypes == null) {
                this.config.allowTypes = [];
            }
            if (this.config.deferred == null) {
                this.config.deferred = false;
            }
            if (this.config.disabled == null) {
                this.config.disabled = false;
            }
            if (this.config.hideDefaultDropZone == null) {
                this.config.hideDefaultDropZone = true;
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
                    this.setDefaultDropzoneVisible();
                }
            } else {
                this.setDefaultDropzoneVisible();
                this.getResultContainer().removeChildren();
                return this;
            }

            let newItemsToAppend: Element[] = [];
            let existingItems: Element[] = [];

            this.parseValues(value).forEach((val) => {
                if (val) {
                    let existingItem = this.getExistingItem(val);
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
            newItemsToAppend.forEach((elem: Element) => this.getResultContainer().appendChild(elem));
        }

        protected removeAllChildrenExceptGiven(itemsToKeep: Element[]) {
            const items: Element[] = this.getResultContainer().getChildren();
            const toRemove = [];

            items.forEach((elem: Element) => {
                if (!itemsToKeep.some((itemToKeep: Element) => itemToKeep === elem)) {
                    toRemove.push(elem);
                }
            });
            toRemove.forEach((elem: Element) => elem.remove());
        }

        protected refreshExistingItem(existingItem: Element, value: string) {
            // must be implemented by children
        }

        protected getExistingItem(value: string): Element {
            return null;
        }

        parseValues(jsonString: string): string[] {
            try {
                let o = JSON.parse(jsonString);

                // Handle non-exception-throwing cases:
                // Neither JSON.parse(false) or JSON.parse(1234) throw errors, hence the type-checking,
                // but... JSON.parse(null) returns 'null', and typeof null === 'object',
                if (o && typeof o === 'object' && o.length) {
                    return o;
                }
            } catch (error) { /* empty*/ }

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
                this.uploader.cancelAll();
            }

            this.uploadedItems.forEach((uploadItem: UploadItem<MODEL>) => uploadItem.notifyUploadStopped());

            this.uploadedItems.length = 0;

            return this;
        }

        reset(): UploaderEl<MODEL> {
            this.setValue(null);
            this.notifyUploadReset();
            this.setProgressVisible(false);
            return this;
        }

        protected getErrorMessage(fileString: string): string {
            return 'File(s) [' + fileString + '] were not uploaded';
        }

        setDefaultDropzoneVisible(visible: boolean = true, isDrag: boolean = false) {
            if (visible && this.config.hideDefaultDropZone && !isDrag) {
                return;
            }

            if (visible) {
                this.setProgressVisible(false);
                this.setResultVisible(false);
            }

            this.defaultDropzoneContainer.toggleClass('visible', visible);
        }

        setProgressVisible(visible: boolean = true) {
            if (visible) {
                this.progress.setValue(0);
                this.setDefaultDropzoneVisible(false);
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
                this.setDefaultDropzoneVisible(false);
                this.setProgressVisible(false);
            }

            this.resultContainer.setVisible(visible);
        }

        createModel(serverResponse: any): MODEL {
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
                    } else if (!this.shownInitHandler) {
                        if (UploaderEl.debug) {
                            console.log('Deferring enabling uploader until it\' shown', this);
                        }
                        this.shownInitHandler = (event: ElementShownEvent) => {
                            this.initHandler();
                            this.unShown(this.shownInitHandler);
                            this.shownInitHandler = null;
                        };
                        this.onShown(this.shownInitHandler);
                    }
                } else {

                    if (this.isRendered()) {
                        this.initHandler();
                    } else if (!this.renderedInitHandler) {
                        if (UploaderEl.debug) {
                            console.log('Deferring enabling uploader until it\' rendered', this);
                        }
                        this.renderedInitHandler = (event: ElementRenderedEvent) => {
                            this.initHandler();
                            this.unRendered(this.renderedInitHandler);
                            this.renderedInitHandler = null;
                        };
                        this.onRendered(this.renderedInitHandler);
                    }
                }
            }
            return this;
        }

        isEnabled(): boolean {
            return !this.config.disabled;
        }

        getParams(): {[key: string]: any} {
            return this.config.params;
        }

        getAllowedTypes(): {title: string; extensions: string}[] {
            return this.config.allowTypes;
        }

        private findUploadItemById(id: number): UploadItem<MODEL> {
            for (let i = 0; i < this.uploadedItems.length; i++) {
                let uploadItem = this.uploadedItems[i];
                if (uploadItem.getId() === String(id)) {
                    return uploadItem;
                }
            }
            return null;
        }

        private submitCallback(id: number, name: string) {
            let file: FineUploaderFile = this.uploader.getFile(id);
            file.id = id;

            if (name.lastIndexOf('.') > 0) {
                name = name.substr(0, name.lastIndexOf('.'));
            }

            let uploadFile = new UploadItem<MODEL>(file);
            this.uploadedItems.push(uploadFile.setName(name));

            this.setProgressVisible();

            this.startUpload();

            this.debouncedUploadStart();
        }

        private statusChangeCallback(id: number, oldStatus: string, newStatus: string) {
            let uploadItem = this.findUploadItemById(id);
            if (!!uploadItem) {
                uploadItem.setStatus(newStatus);
            }
        }

        private progressCallback(id: number, name: string, uploadedBytes: number, totalBytes: number) {
            let percent = Math.round(uploadedBytes / totalBytes * 100);

            this.progress.setValue(percent);

            let uploadItem = this.findUploadItemById(id);
            if (uploadItem) {
                uploadItem.setProgress(percent);
                this.notifyFileUploadProgress(uploadItem);
            }
        }

        private fileCompleteCallback(id: number, name: string, response: any, xhrOrXdr: XMLHttpRequest) {
            if (xhrOrXdr && xhrOrXdr.status === 200) {
                try {
                    let uploadItem = this.findUploadItemById(id);
                    if (uploadItem) {
                        let model: MODEL = this.createModel(JSON.parse(xhrOrXdr.response));
                        uploadItem.setModel(model);
                        this.notifyFileUploaded(uploadItem);
                    }
                } catch (e) {
                    console.warn('Failed to parse the response', response, e);
                }
            }
        }

        private errorCallback(id: number, name: string, errorReason: any, xhrOrXdr: XMLHttpRequest) {
            if (xhrOrXdr && xhrOrXdr.status !== 200) {
                try {
                    let responseObj = JSON.parse(xhrOrXdr.response);
                    let error = new api.rest.RequestError(responseObj.status, responseObj.message);
                    api.DefaultErrorHandler.handle(error);
                } catch (e) {
                    console.warn('Failed to parse the response', xhrOrXdr.response, e);
                    api.notify.NotifyManager.get().showError(this.getErrorMessage(name));
                }

                let uploadItem = this.findUploadItemById(id);
                if (uploadItem) {
                    uploadItem.setModel(null);
                    this.notifyUploadFailed(uploadItem);
                }
                this.finishUpload();
            }
        }

        private allCompleteCallback() {
            let values = [];
            this.uploadedItems.forEach((item) => {
                if (item.getStatus() === qq.status.UPLOAD_SUCCESSFUL) {
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

            this.finishUpload();
        }

        protected initUploader() {
            let uploader = new qq.FineUploaderBasic({
                debug: false,
                button: document.getElementById(this.dropzone.getId()),
                multiple: this.config.allowMultiSelection,
                folders: false,
                autoUpload: false,
                request: {
                    endpoint: this.config.url,
                    params: this.config.params || {},
                    inputName: 'file',
                    filenameParam: 'name'
                },
                validation: {
                    acceptFiles: this.getFileExtensions(this.config.allowTypes),
                },
                text: {
                    fileInputTitle: ''
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
                this.dragAndDropper = new qq.DragAndDrop({
                    dropZoneElements: this.getDropzoneElements(),
                    classes: {
                        dropActive: 'dz-dragover'
                    },
                    callbacks: {
                        //this submits the dropped files to uploader
                        processingDroppedFilesComplete: (files, dropTarget) => {
                            if (!this.isUploading()) {
                                uploader.addFiles(files);
                            }
                        },
                        onDrop: (event: DragEvent) => this.notifyDropzoneDrop(event),
                        onDragEnter: (event: DragEvent) => this.notifyDropzoneDragEnter(event),
                        onDragLeave: (event: DragEvent) => this.notifyDropzoneDragLeave(event)
                    }
                });
            }

            this.disableInputFocus(); // on init
            return uploader;
        }

        private getFileExtensions(allowTypes: {title: string; extensions: string}[]): string {
            let result = '';
            allowTypes.forEach(allowType => {
                if (allowType.extensions) {
                    result += '.' + allowType.extensions.split(',').join(',.') + ',';
                }
            });
            return result;
        }

        addDropzone(id: string) {
            if (this.config.allowDrop) {
                this.extraDropzoneIds.push(id);
                if (this.dragAndDropper) {
                    let elem = document.getElementById(id);
                    if (elem) {
                        this.dragAndDropper.setupExtraDropzone(elem);
                    }
                }
            }
        }

        private getDropzoneElements(): HTMLElement[] {
            let dropElements = [];
            if (this.config.selfIsDropzone) {
                dropElements.push(document.getElementById(this.getId()));
            } else {
                dropElements.push(document.getElementById(this.dropzone.getId()));
            }

            this.extraDropzoneIds.forEach(id => {
                let elem = document.getElementById(id);
                if (elem) {
                    dropElements.push(elem);
                }
            });

            return dropElements;
        }

        private disableInputFocus() {
            let focusableElements: NodeListOf<HTMLInputElement> = this.getDefaultDropzoneContainer().getHTMLElement().getElementsByTagName(
                'input');
            for (let i = 0; i < focusableElements.length; i++) {
                let el = <HTMLInputElement>focusableElements.item(i);
                el.tabIndex = -1;
            }
        }

        private startUpload() {
            this.toggleClass('uploading', true);
        }

        private finishUpload() {
            this.toggleClass('uploading', false);
        }

        private isUploading(): boolean {
            return this.hasClass('uploading');
        }

        getUploadButton(): api.dom.DivEl {
            return this.uploadButton;
        }

        hasUploadButton(): boolean {
            return !!this.config.hasUploadButton;
        }

        getResultContainer(): api.dom.DivEl {
            return this.resultContainer;
        }

        getDefaultDropzoneContainer(): api.dom.DivEl {
            return this.defaultDropzoneContainer;
        }

        getDropzone(): api.dom.AEl {
            return this.dropzone;
        }

        showFileSelectionDialog() {
            wemjq(this.uploader.getInputButton().getInput()).simulate('click');
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent<MODEL>) => void) {
            this.uploadStartedListeners.push(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent<MODEL>) => void) {
            this.uploadStartedListeners = this.uploadStartedListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        onUploadProgress(listener: (event: FileUploadProgressEvent<MODEL>) => void) {
            this.uploadProgressListeners.push(listener);
        }

        unUploadProgress(listener: (event: FileUploadProgressEvent<MODEL>) => void) {
            this.uploadProgressListeners = this.uploadProgressListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        onFileUploaded(listener: (event: FileUploadedEvent<MODEL>) => void) {
            this.fileUploadedListeners.push(listener);
        }

        unFileUploaded(listener: (event: FileUploadedEvent<MODEL>) => void) {
            this.fileUploadedListeners = this.fileUploadedListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        onUploadCompleted(listener: (event: FileUploadCompleteEvent<MODEL>) => void) {
            this.uploadCompleteListeners.push(listener);
        }

        unUploadCompleted(listener: (event: FileUploadCompleteEvent<MODEL>) => void) {
            this.uploadCompleteListeners = this.uploadCompleteListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        onUploadReset(listener: () => void) {
            this.uploadResetListeners.push(listener);
        }

        unUploadReset(listener: () => void) {
            this.uploadResetListeners = this.uploadResetListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        onUploadFailed(listener: (event: FileUploadFailedEvent<MODEL>) => void) {
            this.uploadFailedListeners.push(listener);
        }

        unUploadFailed(listener: (event: FileUploadFailedEvent<MODEL>) => void) {
            this.uploadFailedListeners = this.uploadFailedListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        onDropzoneDragEnter(listener: (event: DragEvent) => void) {
            this.dropzoneDragEnterListeners.push(listener);
        }

        unDropzoneDragEnter(listener: (event: DragEvent) => void) {
            this.dropzoneDragEnterListeners = this.dropzoneDragEnterListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        onDropzoneDragLeave(listener: (event: DragEvent) => void) {
            this.dropzoneDragLeaveListeners.push(listener);
        }

        unDropzoneDragLeave(listener: (event: DragEvent) => void) {
            this.dropzoneDragLeaveListeners = this.dropzoneDragLeaveListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        onDropzoneDrop(listener: (event: DragEvent) => void) {
            this.dropzoneDropListeners.push(listener);
        }

        unDropzoneDragDrop(listener: (event: DragEvent) => void) {
            this.dropzoneDropListeners = this.dropzoneDropListeners.filter((currentListener) => {
                return listener !== currentListener;
            });
        }

        private notifyDropzoneDragEnter(event: DragEvent) {
            this.dropzoneDragEnterListeners.forEach((listener: (event: DragEvent)=>void) => {
                listener(event);
            });
        }

        private notifyDropzoneDragLeave(event: DragEvent) {
            this.dropzoneDragLeaveListeners.forEach((listener: (event: DragEvent)=>void) => {
                listener(event);
            });
        }

        private notifyDropzoneDrop(event: DragEvent) {
            this.dropzoneDropListeners.forEach((listener: (event: DragEvent) => void) => {
                listener(event);
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

    export class DropzoneContainer extends api.dom.DivEl {

        private dropzone: api.dom.AEl;

        constructor(hasMask: boolean = false) {
            super('dropzone-container');
            this.initDropzone();
            if (hasMask) {
                this.appendChild(new api.dom.DivEl('uploader-mask'));
            }
        }

        private initDropzone() {
            this.dropzone = new api.dom.AEl('dropzone');
            this.dropzone.setId('uploader-dropzone-' + new Date().getTime());
            this.dropzone.getEl().setTabIndex(-1);// for mac default settings
            this.dropzone.getEl().setAttribute('data-drop', i18n('drop.file.normal'));
            this.dropzone.getEl().setAttribute('data-drop-default', i18n('drop.clickable'));
            this.getEl().setTabIndex(0);
            this.appendChild(this.dropzone);
        }

        getDropzone(): api.dom.AEl {
            return this.dropzone;
        }
    }
}
