module app_wizard {

    export class ContentWizardPanel extends api_app_wizard.WizardPanel<api_content.Content> {

        private parentContent: api_content.Content;

        private siteContent: api_content.Content;

        private contentType: api_schema_content.ContentType;

        private formIcon: api_app_wizard.FormIcon;

        private contentWizardHeader: api_app_wizard.WizardHeaderWithDisplayNameAndName;

        private contentWizardStepForm: ContentWizardStepForm;

        private pageWizardStepForm: PageWizardStepForm;

        private iconUploadItem: api_ui.UploadItem;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private livePanel: LiveFormPanel;

        private persistAsDraft: boolean;

        constructor(tabId: api_app.AppBarTabId, contentType: api_schema_content.ContentType, parentContent: api_content.Content,
                    persistedContent: api_content.Content, site: api_content.Content) {

            console.log("ContentWizardPanel.constructor started");

            this.persistAsDraft = true;
            this.parentContent = parentContent;
            // TODO: getNearestSite nearest site:
            this.siteContent = site;
            this.contentType = contentType;
            this.contentWizardHeader = new api_app_wizard.WizardHeaderWithDisplayNameAndName();
            var iconUrl = api_content.ContentIconUrlResolver.default();
            this.formIcon = new api_app_wizard.FormIcon(iconUrl, "Click to upload icon",
                api_util.getRestUri("blob/upload"));

            this.formIcon.addListener({

                onUploadFinished: (uploadItem: api_ui.UploadItem) => {

                    this.iconUploadItem = uploadItem;
                    this.formIcon.setSrc(api_util.getRestUri('blob/' + this.iconUploadItem.getBlobKey()));
                }
            });

            var actions = new ContentWizardActions(this);

            var mainToolbar = new ContentWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction(),
                publishAction: actions.getPublishAction()
            });

            var stepToolbar = new api_ui_toolbar.Toolbar();

            var site: api_content.Content = null; // TODO: resolve nearest site content
            this.livePanel = new LiveFormPanel(site);

            this.contentWizardHeader.initNames("", "");
            this.contentWizardHeader.setAutogenerateName(true);

            if (this.parentContent) {
                this.contentWizardHeader.setPath(this.parentContent.getPath().toString() + "/");
            }

            this.contentWizardStepForm = new ContentWizardStepForm();
            var pageWizardStepFormConfig: PageWizardStepFormConfig = {
                parentContent: this.parentContent,
                siteContent: this.siteContent
            };
            this.pageWizardStepForm = new PageWizardStepForm(pageWizardStepFormConfig);

            ShowContentLiveEvent.on((event) => {
                this.toggleFormPanel(false);
            });

            ShowContentFormEvent.on((event) => {
                this.toggleFormPanel(true);
            });

            super({
                tabId: tabId,
                persistedItem: persistedContent,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                stepToolbar: stepToolbar,
                header: this.contentWizardHeader,
                actions: actions,
                livePanel: this.livePanel,
                steps: this.createSteps()
            });

            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            if (contentType.hasContentDisplayNameScript()) {
                this.displayNameScriptExecutor.setScript(contentType.getContentDisplayNameScript());
            }

            console.log("ContentWizardPanel.constructor finished");
        }

        giveInitialFocus() {
            console.log("ContentWizardPanel.giveInitialFocus");
            if (this.isRenderingNew() && !this.contentType.hasContentDisplayNameScript()) {
                this.contentWizardHeader.giveFocus();
            }
            else {
                if (!this.contentWizardStepForm.giveFocus()) {
                    this.contentWizardHeader.giveFocus();
                }
            }
        }

        createSteps(): api_app_wizard.WizardStep[] {
            var steps: api_app_wizard.WizardStep[] = [];
            steps.push(new api_app_wizard.WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm));
            steps.push(new api_app_wizard.WizardStep("Page", this.pageWizardStepForm));
            return steps;
        }

        showCallback() {
            if (this.getPersistedItem()) {
                app.Router.setHash("edit/" + this.getPersistedItem().getId());
            } else {
                app.Router.setHash("new/" + this.contentType.getName());
            }
            super.showCallback();
        }

        renderNew() {
            console.log("ContentWizardPanel.renderNew");
            super.renderNew();

            this.persistNewItem((createdContent: api_content.Content) => {

                this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());
                this.giveInitialFocus();
            });
        }

        renderExisting(content: api_content.Content) {
            super.renderExisting(content);
            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());
        }

        private enableDisplayNameScriptExecution(formView: api_form.FormView) {

            if (this.contentType.hasContentDisplayNameScript()) {
                formView.getEl().addEventListener("keyup", (e) => {
                    this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());
                    var displayName = this.displayNameScriptExecutor.execute();
                    this.contentWizardHeader.setDisplayName(displayName);
                });
            }
        }

        setPersistedItem(content: api_content.Content) {
            super.setPersistedItem(content);

            this.contentWizardHeader.initNames(content.getDisplayName(), content.getName().toString());

            this.formIcon.setSrc(content.getIconUrl());
            var contentData: api_content.ContentData = content.getContentData();

            var formContext = new api_form.FormContextBuilder().
                setParentContent(this.parentContent).
                setPersistedContent(content).
                build();
            this.contentWizardStepForm.renderExisting(formContext, contentData, content.getForm());

            if (content.isPage()) {
                var page = content.getPage();

                new api_content_page.GetPageTemplateByKeyRequest(page.getTemplate()).
                    sendAndParse().
                    done((pageTemplate: api_content_page.PageTemplate) => {

                        this.pageWizardStepForm.renderExisting(content, pageTemplate);


                        this.livePanel.renderExisting(content, pageTemplate);
                    });
            }
        }

        persistNewItem(successCallback?: (createdContent: api_content.Content) => void) {

            var contentData = new api_content.ContentData();

            var createRequest = new api_content.CreateContentRequest().
                setDraft(this.persistAsDraft).
                setName(api_content.ContentUnnamed.newUnnamed()).
                setParent(this.parentContent.getPath()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentType.getForm()).
                setContentData(contentData);

            createRequest.sendAndParse().
                done((createdContent: api_content.Content) => {

                    api_notify.showFeedback('Content was created!');
                    new api_content.ContentCreatedEvent(createdContent).fire();
                    this.setPersistedItem(createdContent);
                    this.getTabId().changeToEditMode(createdContent.getId());

                    if (successCallback) {
                        successCallback.call(this, createdContent);
                    }

                });
        }

        updatePersistedItem(successCallback?: () => void) {

            var updateRequest = new api_content.UpdateContentRequest(this.getPersistedItem().getId()).
                setContentName(this.resolveContentNameForUpdateReuest()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentWizardStepForm.getForm()).
                setContentData(this.contentWizardStepForm.getContentData());

            updateRequest.addAttachments(this.contentWizardStepForm.getFormView().getAttachments());

            if (this.iconUploadItem) {
                var attachment = new api_content.AttachmentBuilder().
                    setBlobKey(this.iconUploadItem.getBlobKey()).
                    setAttachmentName(new api_content.AttachmentName('_thumb.png')).
                    setMimeType(this.iconUploadItem.getMimeType()).
                    setSize(this.iconUploadItem.getSize()).
                    build();
                updateRequest.addAttachment(attachment);
            }

            updateRequest.
                sendAndParse().
                done((updatedContent: api_content.Content) => {

                    api_notify.showFeedback('Content was updated!');
                    new api_content.ContentUpdatedEvent(updatedContent).fire();

                    this.setPersistedItem(updatedContent);

                    if (successCallback) {
                        successCallback.call(this, updatedContent);
                    }
                });
        }

        private resolveContentNameForUpdateReuest(): api_content.ContentName {
            if (api_util.isStringEmpty(this.contentWizardHeader.getName()) && this.getPersistedItem().getName().isUnnamed()) {
                return this.getPersistedItem().getName();
            }
            else {
                return api_content.ContentName.fromString(this.contentWizardHeader.getName());
            }
        }

        hasUnsavedChanges(): boolean {
            var persistedContent: api_content.Content = this.getPersistedItem();
            if (persistedContent == undefined) {
                return true;
            } else {
                return !this.stringsEqual(persistedContent.getDisplayName(), this.contentWizardHeader.getDisplayName())
                           || !this.stringsEqual(persistedContent.getName().toString(), this.contentWizardHeader.getName().toString())
                    || !persistedContent.getContentData().equals(this.contentWizardStepForm.getContentData());
            }
        }

        getParentContent(): api_content.Content {
            return this.parentContent;
        }

        getContentType(): api_schema_content.ContentType {
            return this.contentType;
        }

        private stringsEqual(str1: string, str2: string): boolean {
            // strings are equal if both of them are empty or not specified or they are identical
            return (!str1 && !str2) || (str1 == str2);
        }
    }

}