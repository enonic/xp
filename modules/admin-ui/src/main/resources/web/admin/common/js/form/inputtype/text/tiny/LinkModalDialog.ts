module api.form.inputtype.text.tiny {

    import FormView = api.form.FormView;
    import Form = api.ui.form.Form;
    import FormItem = api.ui.form.FormItem;
    import Panel = api.ui.panel.Panel;
    import DockedPanel = api.ui.panel.DockedPanel;

    export class LinkModalDialog extends ModalDialog {

        private linkTextFormItem: FormItem;
        private mainForm: Form;
        private dockedPanel: DockedPanel;

        constructor(editor: TinyMceEditor) {
            super(editor, new api.ui.dialog.ModalDialogHeader("Insert link"));
        }

        layout() {
            this.appendChildToContentPanel(this.mainForm = this.createMainForm());
            this.appendChildToContentPanel(this.dockedPanel = this.createDockedPanel());
        }

        show() {
            super.show();
            this.linkTextFormItem.getInput().giveFocus();
        }

        private createContentPanel(): Panel {
            return this.createFormPanel([
                <FormItem>this.createContentSelector("Target"),
                this.createTargetCheckbox()
            ]);
        }

        private createDownloadPanel(): Panel {
            return this.createFormPanel([
                <FormItem>this.createContentSelector("Target", api.schema.content.ContentTypeName.getMediaTypes())
            ]);
        }

        private createUrlPanel(): Panel {
            return this.createFormPanel([
                this.createFormItem("Url", true),
                this.createTargetCheckbox()
            ]);
        }

        private createEmailPanel(): Panel {
            return this.createFormPanel([
                this.createFormItem("Email", true),
                this.createFormItem("Subject", false)
            ]);
        }

        private createTargetCheckbox(label?: string): FormItem {
            var checkbox = new api.ui.Checkbox();

            return this.createFormItem("Open in new window", false, checkbox);
        }

        private createMainForm(): Form {
            return this.createForm([
                this.linkTextFormItem = this.createFormItem("Text", true),
                this.createFormItem("Tooltip", false)
            ]);
        }

        private createDockedPanel(): DockedPanel {
            var dockedPanel = new DockedPanel();

            dockedPanel.addItem("Content", this.createContentPanel());
            dockedPanel.addItem("URL", this.createUrlPanel());
            dockedPanel.addItem("Download", this.createDownloadPanel());
            dockedPanel.addItem("Email", this.createEmailPanel());

            return dockedPanel;
        }

        protected initializeActions(existingLink?: boolean) {

            this.addAction(new api.ui.Action(existingLink ? "Update" : "Insert").onExecuted(() => {
                if (this.validate()) {
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private createContentSelector(label: string, contentTypeNames?: api.schema.content.ContentTypeName[]): FormItem {
            var loader = new api.content.ContentSummaryLoader(),
                contentSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build();

            if (contentTypeNames) {
                loader.setAllowedContentTypeNames(contentTypeNames);
            }

            return this.createFormItem(label, true, <api.dom.FormItemEl>contentSelector);
        }

        private validateDockPanel(): boolean {
            var selectedPanel = this.dockedPanel.getDeck().getPanelShown(),
                form = <Form>selectedPanel.getFirstChild();

            return form.validate(true).isValid();
        }

        private validate(): boolean {
            var mainFormValid = this.mainForm.validate(true).isValid();
            var dockPanelValid = this.validateDockPanel();

            return mainFormValid && dockPanelValid;
        }

    }
}
