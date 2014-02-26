module api.ui {

    export interface ImageUploaderConfig {
        multiSelection?: boolean;
        buttonsVisible?: boolean;
        showImageAfterUpload?: boolean;
        maximumOccurrences?: number;
        textInput?: boolean;
        browseEnabled?: boolean;
    }

    export class ImageUploader extends api.dom.FormInputEl implements api.event.Observable {

        private name:string;
        private value:string;
        private uploadUrl:string;
        private uploader;

        private input:api.ui.TextInput;
        private dropzone:api.dom.DivEl;
        private progress:api.ui.ProgressBar;
        private cancelBtn:api.ui.Button;
        private image:api.dom.ImgEl;
        private resetBtn:api.ui.Button;

        private multiSelection:boolean;
        private buttonsVisible:boolean;
        private showImageAfterUpload:boolean;
        private maximumOccurrences:number;
        private browseEnabled:boolean;

        private listeners:ImageUploaderListener[] = [];

        constructor(name:string, uploadUrl:string, config:ImageUploaderConfig = {}) {
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

            this.image = new api.dom.ImgEl();
            this.appendChild(this.image);

            this.cancelBtn = new api.ui.Button("Cancel");
            this.cancelBtn.setVisible(this.buttonsVisible);
            this.cancelBtn.setClickListener(() => {
                this.stop();
                this.reset();
            });
            this.appendChild(this.cancelBtn);

            this.resetBtn = new api.ui.Button("Reset");
            this.resetBtn.setVisible(this.buttonsVisible);
            this.resetBtn.setClickListener(() => {
                this.reset();
            });
            this.appendChild(this.resetBtn);

            this.onRendered((event) => {
                console.log("ImageUploader rendered, creating plupload");
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
                console.log("ImageUploader removed, destroying plupload");
                this.uploader.destroy();
            });
        }

        getName():string {
            return this.name;
        }

        getValue():string {
            return this.value;
        }

        setValue(value:string) {
            this.value = value;
            var src:string;
            if( value && value.indexOf("http://") == -1 ) {
                src = api.util.getAdminUri(value ? 'rest/blob/' + value : 'common/images/x-user-photo.png');
            } else {
                src = value;
            }
            this.image.getEl().setSrc(src);
        }

        setMaximumOccurrences(value:number) {
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
            this.setValue(undefined);
        }

        private setDropzoneVisible(visible:boolean) {
            if (this.input) {
                this.input.setVisible(visible);
            }
            this.dropzone.setVisible(visible);
        }

        private setProgressVisible(visible:boolean) {
            this.progress.setVisible(visible);
            this.cancelBtn.setVisible(visible && this.buttonsVisible);
        }

        private setImageVisible(visible:boolean) {
            this.image.setVisible(visible);
            this.resetBtn.setVisible(visible && this.buttonsVisible);
        }

        private initUploader(elId:string) {

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
                    var responseObj:any = Ext.decode(response.response);

                    if (responseObj.items && responseObj.items.length > 0) {
                        file = responseObj.items[0];

                        this.setValue(file.id);

                        var uploadItem:UploadItem = new UploadItemBuilder().
                            setId(file.id).
                            setName(file.name).
                            setMimeType(file.mimeType).
                            setSize(file.size).
                            build();
                        this.notifyFileUploaded(uploadItem);
                    }
                }

            });

            uploader.bind('UploadComplete', (up, files) => {
                //console.log('uploader upload complete', up, files);

                if (this.showImageAfterUpload) {
                    this.setProgressVisible(false);
                    this.setImageVisible(true);
                }

                this.notifyUploadComplete();

                if (this.uploader.files.length > 0) {
                    this.uploader.splice();
                }
            });

            uploader.init();

            return uploader;
        }

        addListener(listener:ImageUploaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:ImageUploaderListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyFileUploaded(uploadItem:UploadItem) {
            this.listeners.forEach((listener:ImageUploaderListener) => {
                if (listener.onFileUploaded) {
                    listener.onFileUploaded(uploadItem);
                }
            });
        }

        private notifyUploadComplete() {
            this.listeners.forEach((listener:ImageUploaderListener) => {
                if (listener.onUploadComplete) {
                    listener.onUploadComplete();
                }
            });
        }
    }
}