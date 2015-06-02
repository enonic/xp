module api.form.inputtype.text.tiny {

    import FormView = api.form.FormView;
    import Form = api.ui.form.Form;
    import FormItem = api.ui.form.FormItem;
    import Panel = api.ui.panel.Panel;
    import DockedPanel = api.ui.panel.DockedPanel;
    import Validators = api.ui.form.Validators;

    export class LinkModalDialog extends ModalDialog {

        private linkTextFormItem: FormItem;
        private mainForm: Form;
        private dockedPanel: DockedPanel;
        private editor: TinyMceEditor;
        private link: HTMLElement;

        private static tabNames: any = {
            content: "Content",
            url: "URL",
            download: "Download",
            email: "Email"
        };

        private static contentPrefix = "content://";
        private static downloadPrefix = "media://download/";
        private static emailPrefix = "mailto:";
        private static subjectPrefix = "?subject=";

        constructor(editor: TinyMceEditor, link: HTMLElement) {
            this.editor = editor;
            this.link = link;

            super(new api.ui.dialog.ModalDialogHeader("Insert link"));
        }

        private getLinkText(): string {
            return this.link ? this.link["text"] : api.util.StringHelper.EMPTY_STRING;
        }

        private getToolTip(): string {
            return this.link ? this.link.getAttribute("title") : api.util.StringHelper.EMPTY_STRING;
        }

        private isContentLink(): boolean {
            return this.link ? this.link.getAttribute("href").indexOf(LinkModalDialog.contentPrefix) === 0 : false;
        }

        private getContentId(): string {
            if (this.link && this.isContentLink()) {
                return this.link.getAttribute("href").replace(LinkModalDialog.contentPrefix, api.util.StringHelper.EMPTY_STRING);
            }
            return api.util.StringHelper.EMPTY_STRING;
        }

        private isDownloadLink(): boolean {
            return this.link ? this.link.getAttribute("href").indexOf(LinkModalDialog.downloadPrefix) === 0 : false;
        }

        private getDownloadId(): string {
            if (this.isDownloadLink()) {
                return this.link.getAttribute("href").replace(LinkModalDialog.downloadPrefix, api.util.StringHelper.EMPTY_STRING);
            }
            return api.util.StringHelper.EMPTY_STRING;
        }

        private isUrl(): boolean {
            return this.link ? !(this.isContentLink() || this.isDownloadLink() || this.isEmail()) : false;
        }

        private getUrl(): string {
            return this.isUrl() ? this.link.getAttribute("href") : api.util.StringHelper.EMPTY_STRING;
        }

        private isEmail(): boolean {
            return this.link ? this.link.getAttribute("href").indexOf(LinkModalDialog.emailPrefix) === 0 : false;
        }

        private getEmail(): string {
            if (!this.isEmail()) {
                return api.util.StringHelper.EMPTY_STRING;
            }
            var emailArr = this.link.getAttribute("href").split(LinkModalDialog.subjectPrefix);
            return emailArr[0].replace(LinkModalDialog.emailPrefix, api.util.StringHelper.EMPTY_STRING);
        }

        private getSubject(): string {
            if (!this.isEmail()) {
                return api.util.StringHelper.EMPTY_STRING;
            }
            var emailArr = this.link.getAttribute("href").split(LinkModalDialog.subjectPrefix);
            return decodeURI(emailArr[1].replace(LinkModalDialog.subjectPrefix, api.util.StringHelper.EMPTY_STRING));
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
                <FormItem>this.createContentSelector("contentId", "Target", this.getContentId()),
                this.createTargetCheckbox("contentTarget", this.isContentLink())
            ]);
        }

        private createDownloadPanel(): Panel {
            return this.createFormPanel([
                <FormItem>this.createContentSelector("downloadId", "Target", this.getDownloadId(),
                    api.schema.content.ContentTypeName.getMediaTypes())
            ]);
        }

        private createUrlPanel(): Panel {
            return this.createFormPanel([
                this.createFormItem("url", "Url", Validators.required, this.getUrl()),
                this.createTargetCheckbox("urlTarget", this.isUrl())
            ]);
        }

        private createEmailPanel(): Panel {
            var emailFormItem: FormItem = this.createFormItem("email", "Email", LinkModalDialog.validationRequiredEmail, this.getEmail());

            emailFormItem.getLabel().addClass("required");

            return this.createFormPanel([
                emailFormItem,
                this.createFormItem("subject", "Subject", null, this.getSubject())
            ]);

        }

        private static validationRequiredEmail(input: api.dom.FormInputEl): string {
            var isValid;

            if (!(isValid = Validators.required(input))) {
                isValid = Validators.validEmail(input);
            }

            return isValid;
        }

        private getTarget(isTabSelected: boolean): boolean {
            return isTabSelected ? !api.util.StringHelper.isBlank(this.link.getAttribute("target")) : false;
        }

        private createTargetCheckbox(id: string, isTabSelected: boolean): FormItem {
            var checkbox = new api.ui.Checkbox().setChecked(this.getTarget(isTabSelected));

            return this.createFormItem(id, "Open in new window", null, null, checkbox);
        }

        private createMainForm(): Form {
            return this.createForm([
                this.linkTextFormItem = this.createFormItem("linkText", "Text", Validators.required, this.getLinkText()),
                this.createFormItem("toolTip", "Tooltip", null, this.getToolTip())
            ]);
        }

        private createDockedPanel(): DockedPanel {
            var dockedPanel = new DockedPanel();
            dockedPanel.addItem(LinkModalDialog.tabNames.content, this.createContentPanel());
            dockedPanel.addItem(LinkModalDialog.tabNames.url, this.createUrlPanel(), this.isUrl());
            dockedPanel.addItem(LinkModalDialog.tabNames.download, this.createDownloadPanel(), this.isDownloadLink());
            dockedPanel.addItem(LinkModalDialog.tabNames.email, this.createEmailPanel(), this.isEmail());

            return dockedPanel;
        }

        close() {
            super.close();
            this.editor.focus();
        }

        protected initializeActions() {
            this.addAction(new api.ui.Action(this.link ? "Update" : "Insert").onExecuted(() => {
                if (this.validate()) {
                    this.createLink();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private createContentSelector(id: string, label: string, value: string,
                                      contentTypeNames?: api.schema.content.ContentTypeName[]): FormItem {
            var loader = new api.content.ContentSummaryLoader(),
                contentSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build();

            if (contentTypeNames) {
                loader.setAllowedContentTypeNames(contentTypeNames);
            }

            return this.createFormItem(id, label, Validators.required, value, <api.dom.FormItemEl>contentSelector);
        }

        private validateDockPanel(): boolean {
            var form = <Form>this.dockedPanel.getDeck().getPanelShown().getFirstChild();

            return form.validate(true).isValid();
        }

        private validate(): boolean {
            var mainFormValid = this.mainForm.validate(true).isValid();
            var dockPanelValid = this.validateDockPanel();

            this.setValidated();

            return mainFormValid && dockPanelValid;
        }

        private createContentLink(): api.dom.AEl {
            var contentSelector = <api.content.ContentComboBox>this.getFieldById("contentId"),
                targetCheckbox = <api.ui.Checkbox>this.getFieldById("contentTarget");

            var linkEl = new api.dom.AEl();
            linkEl.setUrl(LinkModalDialog.contentPrefix + contentSelector.getValue(), targetCheckbox.isChecked() ? "_blank" : null);

            return linkEl;
        }

        private createDownloadLink(): api.dom.AEl {
            var contentSelector = <api.content.ContentComboBox>this.getFieldById("downloadId");

            var linkEl = new api.dom.AEl();
            linkEl.setUrl(LinkModalDialog.downloadPrefix + contentSelector.getValue());

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
            linkEl.setUrl(LinkModalDialog.emailPrefix + email + (subject ? LinkModalDialog.subjectPrefix + encodeURI(subject) : ""));

            return linkEl;
        }

        updateLink(newLink: api.dom.AEl) {
            var target, title;

            if (title = newLink.getTitle()) {
                this.link.setAttribute("title", title);
            }
            else {
                this.link.removeAttribute("title");
            }
            if (target = newLink.getTarget()) {
                this.link.setAttribute("target", target);
            }
            else {
                this.link.removeAttribute("target");
            }

            this.link.setAttribute("href", newLink.getHref());
            this.link["text"] = newLink.getText();
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
                linkEl = this.createDownloadLink();
                break;
            case (LinkModalDialog.tabNames.email):
                linkEl = this.createEmailLink();
                break;
            }

            linkEl.setHtml(linkText);
            if (toolTip) {
                linkEl.setTitle(toolTip);
            }

            if (this.link) {
                this.updateLink(linkEl);
            }
            else {
                this.editor.insertContent(linkEl.toString());
            }
        }

    }
}
