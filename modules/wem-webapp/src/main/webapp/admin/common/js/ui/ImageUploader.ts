module api.ui {

    export interface ImageUploaderConfig {
        multiSelection?: boolean;
        buttonsVisible?: boolean;
        showImageAfterUpload?: boolean;
        maximumOccurrences?: number;
        textInput?: boolean;
        browseEnabled?: boolean;
    }

    export class ImageUploader extends api.dom.FormInputEl {

        private name: string;
        private value: string;
        private uploadUrl: string;
        private uploader;

        private input: api.ui.TextInput;
        private dropzone: api.dom.DivEl;
        private progress: api.ui.ProgressBar;
        private cancelBtn: api.ui.Button;
        private image: api.dom.ImgEl;
        private imageBox: api.dom.DivEl;
        private resetBtn: api.ui.Button;

        private multiSelection: boolean;
        private buttonsVisible: boolean;
        private showImageAfterUpload: boolean;
        private maximumOccurrences: number;
        private browseEnabled: boolean;

        private imageUploadedListeners: {(event: ImageUploadedEvent):void }[] = [];

        private imageUploadCompleteListeners: { ():void }[] = [];

        private imageResetListeners: {():void }[] = [];

        constructor(name: string, uploadUrl: string, config: ImageUploaderConfig = {}) {
            super("div", "image-uploader");
            this.name = name;
            this.uploadUrl = uploadUrl;
            this.multiSelection = (config.multiSelection == undefined) ? false : config.multiSelection;
            this.buttonsVisible = (config.buttonsVisible == undefined) ? true : config.buttonsVisible;
            this.showImageAfterUpload = (config.showImageAfterUpload == undefined) ? true : config.showImageAfterUpload;
            this.maximumOccurrences = (config.maximumOccurrences == undefined) ? 0 : config.maximumOccurrences;
            this.browseEnabled = (config.browseEnabled == undefined) ? true : config.browseEnabled;

            if (config.textInput) {
                this.input = api.ui.TextInput.middle();
                this.input.setPlaceholder("Paste URL to image here");
                this.appendChild(this.input);
            }

            this.dropzone = new api.dom.DivEl("dropzone");
            // id needed for plupload to init, adding timestamp in case of multiple occurences on page
            this.dropzone.setId('image-uploader-dropzone-' + new Date().getTime());
            this.appendChild(this.dropzone);

            this.progress = new api.ui.ProgressBar();
            this.progress.setClass("progress");
            this.appendChild(this.progress);

            this.imageBox = new api.dom.DivEl();
            this.imageBox.addClass('image-box');
            this.image = new api.dom.ImgEl();
            this.image.onClicked(() => {
                this.image.toggleClass('selected')
            })
            this.imageBox.appendChild(this.image);
            this.appendChild(this.imageBox);

            this.cancelBtn = new api.ui.Button("Cancel");
            this.cancelBtn.setVisible(this.buttonsVisible);
            this.cancelBtn.onClicked((event: MouseEvent) => {
                this.stop();
                this.reset();
            });
            this.appendChild(this.cancelBtn);

            this.resetBtn = new api.ui.Button('');
            this.resetBtn.addClass('icon-close2 icon-large reset');
            this.resetBtn.setVisible(this.buttonsVisible);
            this.resetBtn.onClicked((event: MouseEvent) => {
                this.reset();
            });
            KeyBindings.get().bindKeys([
                new KeyBinding('del', () => {
                    if (this.image.hasClass('selected')) {
                        this.reset()
                    }
                }),
                new KeyBinding('backspace', () => {
                    if (this.image.hasClass('selected')) {
                        this.reset()
                    }
                })
            ])
            this.imageBox.appendChild(this.resetBtn);

            //Image toolbar stub buttons
            //TODO: should be replaced with working buttons later
            var btn1 = new api.ui.Button('');
            btn1.addClass("icon-crop2 icon-large");
            var btn2 = new api.ui.Button('');
            btn2.addClass("icon-rotate icon-large");
            var btn3 = new api.ui.Button('');
            btn3.addClass("icon-rotate2 icon-large");
            var btn4 = new api.ui.Button('');
            btn4.addClass("icon-flip icon-large");
            var btn5 = new api.ui.Button('');
            btn5.addClass("icon-flip2 icon-large");
            var btn6 = new api.ui.Button('');
            btn6.addClass("icon-palette icon-large");

            this.imageBox.appendChild(btn1).appendChild(btn2).appendChild(btn3).appendChild(btn4).appendChild(btn5).appendChild(btn6);
            //End of stub

            this.onRendered((event) => {
                if (!this.uploader && this.uploadUrl) {
                    this.uploader = this.initUploader(this.dropzone.getId());
                }
                if (this.value) {
                    this.setDropzoneVisible(false);
                    this.setProgressVisible(false);
                    this.setImageVisible(true);
                } else {
                    this.setDropzoneVisible(true);
                    this.setProgressVisible(false);
                    this.setImageVisible(false);
                }
            });

            this.onRemoved((event) => {
                this.uploader.destroy();
            });
        }

        getName(): string {
            return this.name;
        }

        getValue(): string {
            return this.value;
        }

        setValue(value: string) {
            this.value = value;
            var src: string;
            if (value && value.indexOf("http://") == -1) {
                src = api.util.getAdminUri(value ? 'rest/blob/' + value : 'common/images/x-user-photo.png');
            } else {
                src = value;
            }

            if (src != null) {
                this.image.getEl().setSrc(src);
            }
        }

        setMaximumOccurrences(value: number) {
            this.maximumOccurrences = value;
        }

        stop() {
            if (this.uploader) {
                this.uploader.stop();
            }
        }

        reset() {
            this.setProgressVisible(false);
            this.setImageVisible(false);
            this.setDropzoneVisible(true);
            this.setValue(null);
            this.image.removeClass('selected');
            this.notifyImageReset();
        }

        private setDropzoneVisible(visible: boolean) {
            if (this.input) {
                this.input.setVisible(visible);
            }
            this.dropzone.setVisible(visible);
        }

        private setProgressVisible(visible: boolean) {
            this.progress.setVisible(visible);
            this.cancelBtn.setVisible(visible && this.buttonsVisible);
        }

        private setImageVisible(visible: boolean) {
            this.imageBox.setVisible(visible);
            this.resetBtn.setVisible(visible && this.buttonsVisible);
        }

        private initUploader(elId: string) {

            if (!plupload) {
                throw new Error("ImageUploader: plupload not found, check if it is included in page.");
            }

            var browseButton = this.browseEnabled ? elId : null;

            var uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: this.multiSelection,
                browse_button: browseButton,
                url: this.uploadUrl,
                multipart: true,
                drop_element: elId,
                flash_swf_url: api.util.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api.util.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ]
            });

            uploader.bind('Init', (up, params) => {
                //console.log('uploader init', up, params);
            });

            uploader.bind('FilesAdded', (up, files) => {
                //console.log('uploader files added', up, files);

                if (this.maximumOccurrences > 0 && files.length > this.maximumOccurrences) {
                    files.splice(this.maximumOccurrences);
                }
            });

            uploader.bind('QueueChanged', (up) => {
                //console.log('uploader queue changed', up);

                up.start();
            });

            uploader.bind('UploadFile', (up, file) => {
                //console.log('uploader upload file', up, file);

                this.setDropzoneVisible(false);
                this.setProgressVisible(true);
            });

            uploader.bind('UploadProgress', (up, file) => {
                //console.log('uploader upload progress', up, file);

                this.progress.setValue(file.percent);
            });

            uploader.bind('FileUploaded', (up, file, response) => {
                //console.log('uploader file uploaded', up, file, response);

                if (response && response.status === 200) {
                    var responseObj: any = Ext.decode(response.response);

                    if (responseObj.items && responseObj.items.length > 0) {
                        file = responseObj.items[0];

                        this.setValue(file.id);

                        var uploadItem: UploadItem = new UploadItemBuilder().
                            setId(file.id).
                            setName(file.name).
                            setMimeType(file.mimeType).
                            setSize(file.size).
                            build();
                        this.notifyImageUploaded(uploadItem);
                    }
                }

            });

            uploader.bind('UploadComplete', (up, files) => {
                //console.log('uploader upload complete', up, files);

                if (this.showImageAfterUpload) {
                    this.setProgressVisible(false);
                    this.setImageVisible(true);
                }

                this.notifyImageUploadComplete();

                if (this.uploader.files.length > 0) {
                    this.uploader.splice();
                }
            });

            uploader.init();

            return uploader;
        }

        onImageUploaded(listener: (event: ImageUploadedEvent)=>void) {
            this.imageUploadedListeners.push(listener);
        }

        unImageUploaded(listener: (event: ImageUploadedEvent)=>void) {
            this.imageUploadedListeners = this.imageUploadedListeners.filter((currentListener: (event: ImageUploadedEvent)=>void) => {
                return listener != currentListener;
            })
        }

        onImageReset(listener: () => void) {
            this.imageResetListeners.push(listener);
        }

        unImageReset(listener: () => void) {
            this.imageResetListeners = this.imageResetListeners.filter((currentListener: ()=> void) => {
                return listener != currentListener;
            })
        }

        onImageUploadComplete(listener: ()=>void) {
            this.imageUploadCompleteListeners.push(listener);
        }

        unImageUploadComplete(listener: ()=>void) {
            this.imageUploadCompleteListeners = this.imageUploadCompleteListeners.filter((currentListener: ()=>void) => {
                return listener != currentListener;
            });
        }

        private notifyImageUploaded(uploadItem: UploadItem) {
            this.imageUploadedListeners.forEach((listener: (event: ImageUploadedEvent)=>void) => {
                listener.call(this, new ImageUploadedEvent(uploadItem));
            });
        }

        private notifyImageUploadComplete() {
            this.imageUploadCompleteListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        private notifyImageReset() {
            this.imageResetListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }
    }
}