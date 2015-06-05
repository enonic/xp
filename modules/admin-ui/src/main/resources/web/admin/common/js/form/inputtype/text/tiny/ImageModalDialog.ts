module api.form.inputtype.text.tiny {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;

    export class ImageModalDialog extends ModalDialog {
        private imageContainer: api.dom.DivEl;

        constructor(editor: TinyMceEditor) {
            super(editor, new api.ui.dialog.ModalDialogHeader("Insert Image"));
        }

        private createImageSelector(id: string, imageId: string): FormItem {
            var loader = new api.content.ContentSummaryLoader(),
                imageSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build(),
                formItem;


            loader.setAllowedContentTypeNames([api.schema.content.ContentTypeName.IMAGE]);

            formItem =
            this.createFormItem(id, api.util.StringHelper.EMPTY_STRING, Validators.required, imageId, <api.dom.FormItemEl>imageSelector);

            return formItem;
        }

        private getImageId(): string {
            return api.util.StringHelper.EMPTY_STRING;
        }

        private getCaption(): string {
            return api.util.StringHelper.EMPTY_STRING;
        }

        protected getMainFormItems(): FormItem[] {
            return [
                this.createImageSelector("imageId", this.getImageId()),
                this.createFormItem("caption", "Caption", Validators.required, this.getCaption())
            ];
        }

        protected initializeActions() {
            this.addAction(new api.ui.Action("Insert").onExecuted(() => {
                if (this.validate()) {
                    this.close();
                }
            }));

            super.initializeActions();
        }
    }
}