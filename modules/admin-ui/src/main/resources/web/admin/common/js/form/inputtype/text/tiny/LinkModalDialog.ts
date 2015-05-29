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
        private editor: TinyMceEditor;

        private static tabNames: any = {
            content: "Content",
            url: "URL",
            download: "Download",
            email: "Email"
        };

        constructor(editor: TinyMceEditor) {
            super(new api.ui.dialog.ModalDialogHeader("Insert link"));
            this.editor = editor;
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
                <FormItem>this.createContentSelector("contentId", "Target"),
                this.createTargetCheckbox("contentTarget")
            ]);
        }

        private createDownloadPanel(): Panel {
            return this.createFormPanel([
                <FormItem>this.createContentSelector("downloadId", "Target", api.schema.content.ContentTypeName.getMediaTypes())
            ]);
        }

        private createUrlPanel(): Panel {
            return this.createFormPanel([
                this.createFormItem("url", "Url", true),
                this.createTargetCheckbox("urlTarget")
            ]);
        }

        private createEmailPanel(): Panel {
            return this.createFormPanel([
                this.createFormItem("email", "Email", true),
                this.createFormItem("subject", "Subject", false)
            ]);
        }

        private createTargetCheckbox(id: string): FormItem {
            var checkbox = new api.ui.Checkbox();

            return this.createFormItem(id, "Open in new window", false, checkbox);
        }

        private createMainForm(): Form {
            return this.createForm([
                this.linkTextFormItem = this.createFormItem("linkText", "Text", true),
                this.createFormItem("toolTip", "Tooltip", false)
            ]);
        }

        private createDockedPanel(): DockedPanel {
            var dockedPanel = new DockedPanel();
            dockedPanel.addItem(LinkModalDialog.tabNames.content, this.createContentPanel());
            dockedPanel.addItem(LinkModalDialog.tabNames.url, this.createUrlPanel());
            dockedPanel.addItem(LinkModalDialog.tabNames.download, this.createDownloadPanel());
            dockedPanel.addItem(LinkModalDialog.tabNames.email, this.createEmailPanel());

            return dockedPanel;
        }

        protected initializeActions(existingLink?: boolean) {

            this.addAction(new api.ui.Action(existingLink ? "Update" : "Insert").onExecuted(() => {
                if (this.validate()) {
                    this.createLink();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private createContentSelector(id: string, label: string, contentTypeNames?: api.schema.content.ContentTypeName[]): FormItem {
            var loader = new api.content.ContentSummaryLoader(),
                contentSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build();

            if (contentTypeNames) {
                loader.setAllowedContentTypeNames(contentTypeNames);
            }

            return this.createFormItem(id, label, true, <api.dom.FormItemEl>contentSelector);
        }

        private validateDockPanel(): boolean {
            var form = <Form>this.dockedPanel.getDeck().getPanelShown().getFirstChild();

            return form.validate(true).isValid();
        }

        private validate(): boolean {
            var mainFormValid = this.mainForm.validate(true).isValid();
            var dockPanelValid = this.validateDockPanel();

            return mainFormValid && dockPanelValid;
        }

        private createContentLink(): api.dom.AEl {
            var contentSelector = <api.content.ContentComboBox>this.getFieldById("contentId"),
                targetCheckbox = <api.ui.Checkbox>this.getFieldById("contentTarget");

            var linkEl = new api.dom.AEl();
            linkEl.setUrl("content://" + contentSelector.getValue(), targetCheckbox.isChecked() ? "_blank" : null);

            return linkEl;
        }

        private createDownloadLink(): api.dom.AEl {
            var contentSelector = <api.content.ContentComboBox>this.getFieldById("downloadId");

            var linkEl = new api.dom.AEl();
            linkEl.setUrl("media://download/" + contentSelector.getValue());

            return linkEl;
        }

        private createUrlLink(): api.dom.AEl {
            var url = (<api.ui.text.TextInput>this.getFieldById("url")).getValue(),
                targetCheckbox = <api.ui.Checkbox>this.getFieldById("urlTarget");

            var linkEl = new api.dom.AEl();
            linkEl.setUrl(url, targetCheckbox.isChecked() ? "_blank" : null);

            return linkEl;
        }

        private createEmailLink(): api.dom.AEl {
            var email = (<api.ui.text.TextInput>this.getFieldById("email")).getValue(),
                subject = (<api.ui.text.TextInput>this.getFieldById("subject")).getValue();

            var linkEl = new api.dom.AEl();
            linkEl.setUrl("mailto:" + email + (subject ? "?subject:" + encodeURI(subject) : ""));

            return linkEl;
        }

        private createLink(): void {
            var linkEl: api.dom.AEl,
                deck = <api.ui.panel.NavigatedDeckPanel>this.dockedPanel.getDeck(),
                selectedTab = <api.ui.tab.TabBarItem>deck.getSelectedNavigationItem(),
                linkText: string = (<api.ui.text.TextInput>this.getFieldById("linkText")).getValue(),
                toolTip: string = (<api.ui.text.TextInput>this.getFieldById("toolTip")).getValue();

            switch (selectedTab.getLabel()) {
            case (LinkModalDialog.tabNames.content):
                linkEl = this.createContentLink();
                break;
            case (LinkModalDialog.tabNames.url):
                linkEl = this.createUrlLink();
                break;
            case (LinkModalDialog.tabNames.download):
                linkEl = this.createContentLink();
                break;
            case (LinkModalDialog.tabNames.email):
                linkEl = this.createEmailLink();
                break;
            }

            linkEl.setHtml(linkText);
            if (toolTip) {
                linkEl.setTitle(toolTip);
            }

            this.editor.insertContent(linkEl.toString());
        }

    }
}
