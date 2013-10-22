declare var plupload;

module api_form_input_type {


    export interface ImageConfig {
        rows:number;
        columns:number;
    }


    export class Image extends BaseInputTypeView {

        constructor(config?:ImageConfig) {
            super("Image");
            this.addClass("image");
        }

        createInputOccurrenceElement(index:number, property:api_data.Property):api_dom.Element {

            var inputEl = new ImageUploader(this.getInput().getName() + "-" + index, api_util.getRestUri("upload"));
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <ImageUploader>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }
    }


    class ImageUploader extends api_dom.FormInputEl {

        private name:string;
        private value:string;
        private uploadUrl:string;
        private uploader;

        private input:api_ui.TextInput;
        private dropzone:api_dom.DivEl;
        private progress:api_ui.ProgressBar;
        private cancelBtn:api_ui.Button;
        private image:api_dom.ImgEl;
        private resetBtn:api_ui.Button;

        constructor(name:string, uploadUrl:string) {
            super("div", "ImageUploader", "image-uploader");
            this.name = name;
            this.uploadUrl = uploadUrl;

            this.input = api_ui.TextInput.middle();
            this.input.setPlaceholder("Paste URL to image here");
            this.appendChild(this.input);

            this.dropzone = new api_dom.DivEl("DropZone", "dropzone");
            this.dropzone.getEl().setInnerHtml("Drop files here or click to select");
            this.appendChild(this.dropzone);

            this.progress = new api_ui.ProgressBar();
            this.progress.setClass("progress");
            this.appendChild(this.progress);

            this.image = new api_dom.ImgEl();
            this.appendChild(this.image);

            this.cancelBtn = new api_ui.Button("Cancel");
            this.cancelBtn.setClickListener(() => {
                this.cancel();
            });
            this.appendChild(this.cancelBtn);

            this.resetBtn = new api_ui.Button("Reset");
            this.resetBtn.setClickListener(() => {
                this.reset();
            });
            this.appendChild(this.resetBtn);
        }

        afterRender() {
            super.afterRender();
            if (!this.uploader && this.uploadUrl) {
                this.uploader = this.initUploader(this.dropzone.getId());
            }
            this.setProgressVisible(false);
            this.setImageVisible(false);
        }

        getName() {
            return this.name;
        }

        getValue() {
            return this.value;
        }

        setValue(value:string) {
            this.value = value;
            var src = api_util.getAdminUri(value ? 'rest/upload/' + value : 'resources/images/x-user-photo.png');
            this.image.getEl().setSrc(src);
        }

        private cancel() {
            if (this.uploader) {
                this.uploader.stop();
            }
            this.setProgressVisible(false);
            this.setDropzoneVisible(true);
        }

        private reset() {
            this.setImageVisible(false);
            this.setDropzoneVisible(true);
            this.setValue(undefined);
        }

        private setDropzoneVisible(visible:boolean) {
            this.input.setVisible(visible);
            this.dropzone.setVisible(visible);
        }

        private setProgressVisible(visible:boolean) {
            this.progress.setVisible(visible);
            this.cancelBtn.setVisible(visible);
        }

        private setImageVisible(visible:boolean) {
            this.image.setVisible(visible);
            this.resetBtn.setVisible(visible);
        }

        private initUploader(elId:string) {

            if (!plupload) {
                throw new Error("ImageUploader: plupload not found, check if it is included in page.");
            }

            var uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: false,
                browse_button: elId,
                url: this.uploadUrl,
                multipart: true,
                drop_element: elId,
                flash_swf_url: api_util.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api_util.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ]
            });

            uploader.bind('Init', function (up, params) {
                console.log('uploader init', up, params);
            });

            uploader.bind('FilesAdded', function (up, files) {
                console.log('uploader files added', up, files);
            });

            uploader.bind('QueueChanged', function (up) {
                console.log('uploader queue changed', up);

                up.start();
            });

            uploader.bind('UploadFile', (up, file) => {
                console.log('uploader upload file', up, file);

                this.setDropzoneVisible(false);
                this.setProgressVisible(true);
            });

            uploader.bind('UploadProgress', (up, file) => {
                console.log('uploader upload progress', up, file);

                this.progress.setValue(file.percent);
            });

            uploader.bind('FileUploaded', (up, file, response) => {
                console.log('uploader file uploaded', up, file, response);

                if (response && response.status === 200) {

                    var responseObj:any = Ext.decode(response.response);
                    var id = (responseObj.items && responseObj.items.length > 0) ? responseObj.items[0].id : undefined;

                    this.setValue(id);
                }

            });

            uploader.bind('UploadComplete', (up, files) => {
                console.log('uploader upload complete', up, files);

                this.setProgressVisible(false);
                this.setImageVisible(true);
            });

            uploader.init();

            return uploader;
        }

    }


    api_form_input.InputTypeManager.register("Image", Image);

}