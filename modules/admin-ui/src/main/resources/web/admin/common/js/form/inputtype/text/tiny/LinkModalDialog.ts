module api.form.inputtype.text.tiny {

    import FormItem = api.ui.form.FormItem;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import Validators = api.ui.form.Validators;

    export class LinkModalDialog extends api.ui.dialog.ModalDialog {

        private linkTextFormItem: FormItem;
        private editor: TinyMceEditor;

        constructor(editor: TinyMceEditor) {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Insert link")
            });

            this.editor = editor;
            this.getEl().addClass("tinymce-link-modal-dialog");

            this.appendChildToContentPanel(this.createMainForm());
            this.appendChildToContentPanel(this.createDockedPanel());

            this.initializeActions();
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
            this.linkTextFormItem.getInput().giveFocus();
        }

        private createForm(formItems: FormItem[]): api.ui.form.Form {
            var form = new api.ui.form.Form(),
                fieldSet = new api.ui.form.Fieldset();

            formItems.forEach((formItem: FormItem) => {
                fieldSet.add(formItem);
            });

            form.add(fieldSet);

            return form;
        }

        private createMainForm(): api.ui.form.Form {
            return this.createForm([
                this.linkTextFormItem = this.createFormItem("Text", true),
                this.createFormItem("Tooltip", false)
            ]);
        }

        private createFormPanel(formItems: FormItem[]): api.ui.panel.Panel {
            var panel = new api.ui.panel.Panel(),
                form = this.createForm(formItems);

            panel.appendChild(form);

            return panel;
        }

        private createContentPanel(): api.ui.panel.Panel {
            return this.createFormPanel([
                <FormItem>this.createContentSelector("Target"),
                this.createTargetCheckbox()
            ]);
        }

        private createDownloadPanel(): api.ui.panel.Panel {
            return this.createFormPanel([
                <FormItem>this.createContentSelector("Target", api.schema.content.ContentTypeName.getMediaTypes())
            ]);
        }

        private createUrlPanel(): api.ui.panel.Panel {
            return this.createFormPanel([
                this.createFormItem("Url", true),
                this.createTargetCheckbox()
            ]);
        }

        private createEmailPanel(): api.ui.panel.Panel {
            return this.createFormPanel([
                this.createFormItem("Email", true),
                this.createFormItem("Subject", false)
            ]);
        }

        private createTargetCheckbox(label?: string): FormItem {
            var checkbox = new api.ui.Checkbox();

            return this.createFormItem("Open in new window", false, checkbox);
        }

        private createFormItem(label: string, required: boolean, inputEl?: api.dom.FormItemEl): FormItem {
            var formItemBuilder = new FormItemBuilder(inputEl || new api.ui.text.TextInput()).setLabel(label);

            if (required) {
                formItemBuilder.setValidator(Validators.required);
            }

            return formItemBuilder.build();
        }

        private createDockedPanel(): api.ui.panel.DockedPanel {
            var dockedPanel = new api.ui.panel.DockedPanel();

            dockedPanel.addItem("Content", this.createContentPanel());
            dockedPanel.addItem("URL", this.createUrlPanel());
            dockedPanel.addItem("Download", this.createDownloadPanel());
            dockedPanel.addItem("Email", this.createEmailPanel());

            return dockedPanel;
        }

        private initializeActions(existingLink?: boolean) {

            this.addAction(new api.ui.Action(existingLink ? "Update" : "Insert").onExecuted(() => {
                this.close();
            }));

            this.addCancelButtonToBottom();
        }

        private createContentSelector(label: string, contentTypeNames?: api.schema.content.ContentTypeName[]): FormItem {
            var loader = new api.content.ContentSummaryLoader(),
                contentSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build();

            if (contentTypeNames) {
                loader.setAllowedContentTypeNames(contentTypeNames);
            }

            return this.createFormItem(label, true, <api.dom.FormItemEl>contentSelector);
        }
    }
}
