module api.ui.uploader {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface FileUploaderConfig {
        name: string;
        url: string;
        allowBrowse?: boolean;
        allowTypes?: {title: string; extensions: string}[];
        allowMultiSelection?: boolean;
        showInput?: boolean;
        showButtons?: boolean;
        showResult?: boolean;
        maximumOccurrences?: number;
        deferred?: boolean;
    }

    export class FileUploader extends api.dom.FormInputEl {

        private config: FileUploaderConfig;
        private uploader;
        private value;
        private uploadedItems: UploadItem[] = [];

        private input: api.ui.text.TextInput;
        private dropzone: api.dom.DivEl;

        private progress: api.ui.ProgressBar;
        private cancelBtn: Button;

        private result: api.dom.DivEl;
        private resetBtn: CloseButton;

        private uploadStartedListeners: {(event: FileUploadStartedEvent):void }[] = [];
        private uploadProgressListeners: {(event: FileUploadProgressEvent):void }[] = [];
        private fileUploadedListeners: {(event: FileUploadedEvent):void }[] = [];
        private uploadCompleteListeners: { (event: FileUploadCompleteEvent):void }[] = [];
        private uploadResetListeners: {():void }[] = [];

        constructor(config: FileUploaderConfig) {
            super("div", "file-uploader");

            this.config = config;
            // init defaults
            if (this.config.showResult == undefined) {
                this.config.showResult = true;
            }
            if (this.config.allowMultiSelection == undefined) {
                this.config.allowMultiSelection = false;
            }
            if (this.config.showButtons == undefined) {
                this.config.showButtons = true;
            }
            if (this.config.maximumOccurrences == undefined) {
                this.config.maximumOccurrences = 0;
            }
            if (this.config.allowBrowse == undefined) {
                this.config.allowBrowse = true;
            }
            if (this.config.allowTypes == undefined) {
                this.config.allowTypes = [];
            }
            if (this.config.deferred == undefined) {
                this.config.deferred = false;
            }

            if (config.showInput) {
                this.input = api.ui.text.TextInput.middle();
                this.input.setPlaceholder("Paste URL to image here");
                this.appendChild(this.input);
            }

            this.dropzone = new api.dom.DivEl("dropzone");
            // id needed for plupload to init, adding timestamp in case of multiple occurrences on page
            this.dropzone.setId('image-uploader-dropzone-' + new Date().getTime());
            this.appendChild(this.dropzone);

            this.progress = new api.ui.ProgressBar();
            this.appendChild(this.progress);

            this.result = new api.dom.DivEl('result');
            this.appendChild(this.result);

            this.cancelBtn = new Button("Cancel");
            this.cancelBtn.setVisible(this.config.showButtons);
            this.cancelBtn.onClicked((event: MouseEvent) => {
                this.stop();
                this.reset();
            });
            this.appendChild(this.cancelBtn);

            this.resetBtn = new CloseButton();
            this.resetBtn.setVisible(this.config.showButtons);
            this.resetBtn.onClicked((event: MouseEvent) => {
                this.reset();
            });
            this.appendChild(this.resetBtn);

            var resetHandler = () => {
                this.reset();
                return false;
            };
            KeyBindings.get().bindKeys([
                new KeyBinding('del', resetHandler),
                new KeyBinding('backspace', resetHandler)
            ]);

            var initHandler = (event) => {
                if (!this.uploader && this.config.url) {
                    this.uploader = this.initUploader(this.dropzone.getId());

                    if (this.value) {
                        this.setValue(this.value);
                    } else {
                        this.setDropzoneVisible();
                    }
                }
            };
            if (this.config.deferred) {
                this.onShown((event) => initHandler(event))
            } else {
                this.onRendered((event) => initHandler(event));
            }

            this.onRemoved((event) => {
                this.uploader.destroy();
            });
        }

        getName(): string {
            return this.config.name;
        }

        getValue(): string {
            return this.value;
        }

        setValue(value: string): FileUploader {
            this.value = value;

            if (value && this.config.showResult) {
                this.setResultVisible();
            } else {
                this.setDropzoneVisible();
            }
            return this;
        }

        setMaximumOccurrences(value: number): FileUploader {
            this.config.maximumOccurrences = value;
            return this;
        }

        stop() {
            if (this.uploader) {
                this.uploader.stop();
            }
        }

        reset() {
            this.setValue(null);
            this.notifyFileUploadReset();
        }

        private setDropzoneVisible(visible: boolean = true) {
            if (visible) {
                this.setProgressVisible(false);
                this.setResultVisible(false);
            }


            if (this.input) {
                this.input.setVisible(visible);
            }
            this.dropzone.setVisible(visible);
        }

        private setProgressVisible(visible: boolean = true) {
            if (visible) {
                this.setDropzoneVisible(false);
                this.setResultVisible(false);
            }

            this.progress.setVisible(visible);
            this.cancelBtn.setVisible(visible && this.config.showButtons);
        }

        private setResultVisible(visible: boolean = true) {
            if (visible) {
                this.setDropzoneVisible(false);
                this.setProgressVisible(false);
            }

            this.result.setVisible(visible);
            this.resetBtn.setVisible(visible && this.config.showButtons);
        }

        private createUploadItem(file): UploadItem {
            return new UploadItemBuilder().
                setId(file.id).
                setName(file.name).
                setMimeType(file.type).
                setProgress(file.loaded).
                setSize(file.size).
                build();
        }

        private findUploadItemById(id: string): UploadItem {
            for (var i = 0; i < this.uploadedItems.length; i++) {
                var item = this.uploadedItems[i];
                if (item.getId() == id) {
                    return item;
                }
            }
            return undefined;
        }

        private initUploader(elId: string) {

            if (!plupload) {
                throw new Error("FileUploader: plupload not found, check if it is included in page.");
            }

            var uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: this.config.allowMultiSelection,
                browse_button: this.config.allowBrowse ? elId : undefined,
                url: this.config.url,
                multipart: true,
                drop_element: elId,
                flash_swf_url: api.util.UriHelper.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api.util.UriHelper.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: this.config.allowTypes
            });

            uploader.bind('Init', (up, params) => {
                console.log('uploader init', up, params);
            });

            uploader.bind('FilesAdded', (up, files) => {
                console.log('uploader files added', up, files);

                if (this.config.maximumOccurrences > 0 && files.length > this.config.maximumOccurrences) {
                    files.splice(this.config.maximumOccurrences);
                }

                files.forEach((file) => {
                    this.uploadedItems.push(this.createUploadItem(file));
                });

                this.setProgressVisible();

                // detach from the main thread
                setTimeout(function () {
                    up.start();
                }, 1);

                this.notifyFileUploadStarted(this.uploadedItems);
            });

            uploader.bind('QueueChanged', (up) => {
                console.log('uploader queue changed', up);
            });

            uploader.bind('UploadFile', (up, file) => {
                console.log('uploader upload file', up, file);
            });

            uploader.bind('UploadProgress', (up, file) => {
                console.log('uploader upload progress', up, file);

                this.progress.setValue(file.percent);

                this.notifyFileUploadProgress(this.findUploadItemById(file.id).setProgress(file.percent));
            });

            uploader.bind('FileUploaded', (up, file, response) => {
                console.log('uploader file uploaded', up, file, response);

                if (response && response.status === 200) {
                    try {
                        var responseObj: any = JSON.parse(response.response);

                        if (responseObj.items && responseObj.items.length > 0) {
                            var uploadedFile = responseObj.items[0];

                            var uploadItem = this.findUploadItemById(file.id).
                                setBlobKey(new api.blob.BlobKey(uploadedFile.id)).
                                setMimeType(uploadedFile.mimeType).
                                setProgress(100);

                            this.notifyFileUploaded(uploadItem);
                        }
                    } catch (e) {
                        console.warn("Failed to parse the response", response, e);
                    }
                }

            });

            uploader.bind('UploadComplete', (up, files) => {
                console.log('uploader upload complete', up, files);

                if (this.uploadedItems.length > 0) {
                    var keys = this.uploadedItems.map((item) => {
                        return item.getBlobKey().toString();
                    });

                    this.setValue(JSON.stringify(keys));

                    this.notifyUploadCompleted(this.uploadedItems);
                    this.uploadedItems.length = 0;
                }
                if (this.uploader.files.length > 0) {
                    this.uploader.splice();
                }
            });

            uploader.init();

            return uploader;
        }

        getResultContainer(): api.dom.DivEl {
            return this.result;
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent)=>void) {
            this.uploadStartedListeners.push(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent)=>void) {
            this.uploadStartedListeners = this.uploadStartedListeners.filter((currentListener: (event: FileUploadStartedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        onUploadProgress(listener: (event: FileUploadProgressEvent)=>void) {
            this.uploadProgressListeners.push(listener);
        }

        unUploadProgress(listener: (event: FileUploadProgressEvent)=>void) {
            this.uploadProgressListeners = this.uploadProgressListeners.filter((currentListener) => {
                return listener != currentListener;
            });
        }

        onFileUploaded(listener: (event: FileUploadedEvent)=>void) {
            this.fileUploadedListeners.push(listener);
        }

        unFileUploaded(listener: (event: FileUploadedEvent)=>void) {
            this.fileUploadedListeners = this.fileUploadedListeners.filter((currentListener: (event: FileUploadedEvent)=>void) => {
                return listener != currentListener;
            })
        }

        onUploadCompleted(listener: (event: FileUploadCompleteEvent)=>void) {
            this.uploadCompleteListeners.push(listener);
        }

        unUploadComplete(listener: (event: FileUploadCompleteEvent)=>void) {
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

        private notifyFileUploadStarted(uploadItems: UploadItem[]) {
            this.uploadStartedListeners.forEach((listener: (event: FileUploadStartedEvent)=>void) => {
                listener(new FileUploadStartedEvent(uploadItems));
            });
        }

        private notifyFileUploadProgress(uploadItem: UploadItem) {
            this.uploadProgressListeners.forEach((listener: (event: FileUploadProgressEvent)=>void) => {
                listener(new FileUploadProgressEvent(uploadItem));
            });
        }

        private notifyFileUploaded(uploadItem: UploadItem) {
            this.fileUploadedListeners.forEach((listener: (event: FileUploadedEvent)=>void) => {
                listener.call(this, new FileUploadedEvent(uploadItem));
            });
        }

        private notifyUploadCompleted(uploadItems: UploadItem[]) {
            this.uploadCompleteListeners.forEach((listener: (event: FileUploadCompleteEvent)=>void) => {
                listener(new FileUploadCompleteEvent(uploadItems));
            });
        }

        private notifyFileUploadReset() {
            this.uploadResetListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }
    }
}