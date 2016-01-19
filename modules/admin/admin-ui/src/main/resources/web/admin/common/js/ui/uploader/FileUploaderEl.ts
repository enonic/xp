module api.ui.uploader {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import Element = api.dom.Element;
    import SelectionItem = api.app.browse.SelectionItem;


    export class FileUploaderEl<MODEL extends api.Equitable> extends UploaderEl<MODEL> {

        protected contentId: string;

        static FILE_NAME_DELIMITER = "/";

        setValue(value: string): UploaderEl<MODEL> {

            var newItemsToAppend: Element[] = [];
            this.value = value;

            if (UploaderEl.debug) {
                console.log('Setting uploader value', value, this);
            }

            this.parseValues(value).forEach((parsedValue: string) => {
                if (parsedValue) {

                    parsedValue.split(FileUploaderEl.FILE_NAME_DELIMITER).forEach((curValue) => {
                        var existingItems:Element[] = [];

                        var existingItem = this.getExistingItem(curValue);
                        if (!existingItem) {
                            newItemsToAppend.push(this.createResultItem(curValue));
                        } else {
                            existingItems.push(existingItem);
                        }
                    });

                    if (this.config.showResult) {
                        this.setResultVisible();
                    } else {
                        this.setDropzoneVisible();
                    }
                }
            });

            this.appendNewItems(newItemsToAppend);


            this.getDropzoneContainer().setVisible(false);
            this.getDropzone().setVisible(false);
            return this;
        }

        setContentId(contentId: string) {
            this.contentId = contentId;
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
                }
            }
        }

    }
}