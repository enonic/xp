module app_wizard {

    export class ContentWizardPanel extends api_app_wizard.WizardPanel<api_content.Content> {

        private static DEFAULT_CONTENT_ICON_URL: string = api_util.getAdminUri("common/images/default_content.png");

        private parentContent: api_content.Content;

        private siteContent: api_content.Content;

        private contentType: api_schema_content.ContentType;

        private formIcon: api_app_wizard.FormIcon;

        private contentWizardHeader: api_app_wizard.WizardHeaderWithDisplayNameAndName;

        private contentWizardStepForm: ContentWizardStepForm;

        private pageWizardStepForm: PageWizardStepForm;

        private iconUploadId: string;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private livePanel: LiveFormPanel;

        private persistAsDraft:boolean;

        constructor(tabId: api_app.AppBarTabId, contentType: api_schema_content.ContentType, parentContent: api_content.Content) {

            this.persistAsDraft = true;
            this.parentContent = parentContent;
            // TODO: getNearestSite nearest site:
            // this.siteContent = this.getNearestSite(content.getContentId());
            this.contentType = contentType;
            this.contentWizardHeader = new api_app_wizard.WizardHeaderWithDisplayNameAndName();
            var iconUrl = contentType.getIcon() ? contentType.getIcon() : ContentWizardPanel.DEFAULT_CONTENT_ICON_URL;
            this.formIcon = new api_app_wizard.FormIcon(iconUrl, "Click to upload icon",
                api_util.getRestUri("upload"));

            this.formIcon.addListener({

                onUploadFinished: (uploadId: string, mimeType: string, uploadName: string) => {

                    this.iconUploadId = uploadId;

                    this.formIcon.setSrc(api_util.getRestUri('upload/' + uploadId));
                }
            });

            var actions = new ContentWizardActions(this);

            var mainToolbar = new ContentWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            var stepToolbar = new api_ui_toolbar.Toolbar();
            stepToolbar.addAction(actions.getPublishAction());

            var site: api_content.Content = null; // TODO: resolve nearest site content
            this.livePanel = new LiveFormPanel(site);

            this.contentWizardHeader.initNames("New " + this.contentType.getDisplayName(), null);
            this.contentWizardHeader.setAutogenerateName(true);

            this.contentWizardStepForm = new ContentWizardStepForm();
            var pageWizardStepFormConfig: PageWizardStepFormConfig = {
                parentContent: this.parentContent,
                siteContent: this.siteContent
            };
            this.pageWizardStepForm = new PageWizardStepForm(pageWizardStepFormConfig);

            super({
                tabId: tabId,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                stepToolbar: stepToolbar,
                header: this.contentWizardHeader,
                actions: actions,
                livePanel: this.livePanel,
                steps: this.createSteps()
            });

            ShowContentLiveEvent.on((event) => {
                this.toggleFormPanel(false);
            });

            ShowContentFormEvent.on((event) => {
                this.toggleFormPanel(true);
            });

            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            if (contentType.getContentDisplayNameScript()) {
                this.displayNameScriptExecutor.setScript(contentType.getContentDisplayNameScript());

                this.getEl().addEventListener("keyup", (e) => {

                    this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());

                    var displayName = this.displayNameScriptExecutor.execute();

                    this.contentWizardHeader.setDisplayName(displayName);
                });
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
            super.renderNew();

            this.contentWizardStepForm.renderNew(this.contentType.getForm());
            // TODO: GetPageTemplateRequest use descriptor config form
            this.pageWizardStepForm.renderNew();
            this.persistNewItem();

            this.livePanel.renderNew();
        }

        setPersistedItem(content: api_content.Content) {
            super.setPersistedItem(content);

            this.contentWizardHeader.initNames(content.getDisplayName(), content.getName());
            // setup displayName and name to be generated automatically
            // if corresponding values are empty
            this.contentWizardHeader.setAutogenerateName(!content.getName());

            this.formIcon.setSrc(content.getIconUrl());
            var contentData: api_content.ContentData = content.getContentData();

            this.contentWizardStepForm.renderExisting(contentData, content.getForm());

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

        persistNewItem(successCallback?: (contentId: string, contentPath: string) => void) {

            var contentData = this.contentWizardStepForm.getContentData();

            var createRequest = new api_content.CreateContentRequest().
                setDraft(this.persistAsDraft).
                setName(this.contentWizardHeader.getName()).
                setParent(this.parentContent.getPath()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentWizardStepForm.getForm()).
                setContentData(contentData);

            if (this.iconUploadId) {
                createRequest.addAttachment(new api_content.Attachment(this.iconUploadId, new api_content.AttachmentName('_thumb.png')));
            }
            var attachments: api_content.Attachment[] = this.contentWizardStepForm.getFormView().getAttachments();
            createRequest.addAttachments(attachments);

            createRequest.sendAndParse().done((createdContent: api_content.Content) => {

                api_notify.showFeedback('Content was created!');
                new api_content.ContentCreatedEvent(createdContent).fire();
                this.setPersistedItem(createdContent);
                this.getTabId().changeToEditMode(createdContent.getId());

                if (successCallback) {
                    successCallback.call(this, createdContent.getContentId().toString(), createdContent.getPath().toString());
                }

            });
        }

        updatePersistedItem(successCallback?: () => void) {

            var updateRequest = new api_content.UpdateContentRequest(this.getPersistedItem().getId()).
                setContentName(this.contentWizardHeader.getName()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentWizardStepForm.getForm()).
                setContentData(this.contentWizardStepForm.getContentData());

            if (this.iconUploadId) {
                updateRequest.addAttachment(new api_content.Attachment(this.iconUploadId, new api_content.AttachmentName('_thumb.png')));
            }

            updateRequest.send().done((updateResponse: api_rest.JsonResponse<any>) => {
                var json = updateResponse.getJson();
                if (json.error) {
                    api_notify.showError(json.error.message);
                } else {
                    api_notify.showFeedback('Content was updated!');
                    var content: api_content.Content = new api_content.Content(json.result);
                    new api_content.ContentUpdatedEvent(content).fire();
                    this.setPersistedItem(content);

                    if (successCallback) {
                        successCallback.call(this, json.contentId, json.contentPath);
                    }
                }

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }

        hasUnsavedChanges(): boolean {
            var persistedContent: api_content.Content = this.getPersistedItem();
            if (persistedContent == undefined) {
                return true;
            } else {
                return !this.stringsEqual(persistedContent.getDisplayName(), this.contentWizardHeader.getDisplayName())
                           || !this.stringsEqual(persistedContent.getName(), this.contentWizardHeader.getName())
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

        private getNearestSite(content: api_content.ContentId): api_content.Content {
            // TODO: CMS-2534
            return null;
        }
    }


}