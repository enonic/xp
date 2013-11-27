module app_wizard {

    export class ContentWizardPanel extends api_app_wizard.WizardPanel<api_content.Content> {

        private static DEFAULT_CONTENT_ICON_URL:string = api_util.getAdminUri("common/images/default_content.png");

        private persistedDraft:api_content.Content;

        private parentContent:api_content.Content;

        private contentType:api_schema_content.ContentType;

        private formIcon:api_app_wizard.FormIcon;

        private contentWizardHeader:api_app_wizard.WizardHeaderWithDisplayNameAndName;

        private contentForm:ContentForm;

        private iconUploadId:string;

        private displayNameScriptExecutor:DisplayNameScriptExecutor;

        constructor(tabId:api_app.AppBarTabId, contentType:api_schema_content.ContentType, parentContent:api_content.Content) {

            this.parentContent = parentContent;
            this.contentType = contentType;
            this.contentWizardHeader = new api_app_wizard.WizardHeaderWithDisplayNameAndName();
            this.formIcon = new api_app_wizard.FormIcon(ContentWizardPanel.DEFAULT_CONTENT_ICON_URL, "Click to upload icon",
                api_util.getRestUri("upload"));

            this.formIcon.addListener({

                onUploadFinished: (uploadId:string, mimeType:string, uploadName:string) => {

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

            var livePanel = new LiveFormPanel();

            this.contentWizardHeader.initNames("New " + this.contentType.getDisplayName(), null);
            this.contentWizardHeader.setAutogenerateName(true);

            super({
                tabId: tabId,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                stepToolbar: stepToolbar,
                header: this.contentWizardHeader,
                actions: actions,
                livePanel: livePanel,
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

                    this.displayNameScriptExecutor.setFormView(this.contentForm.getFormView());

                    var displayName = this.displayNameScriptExecutor.execute();

                    this.contentWizardHeader.setDisplayName(displayName);
                });
            }
        }

        createSteps():api_app_wizard.WizardStep[] {
            var steps:api_app_wizard.WizardStep[] = [];
            this.contentForm = new ContentForm();
            steps.push(new api_app_wizard.WizardStep(this.contentType.getDisplayName(), this.contentForm));
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

            this.contentForm.renderNew(this.contentType.getForm());
            //this.persistNewDraft();
        }

        setPersistedDraft(value:api_content.Content) {
            this.persistedDraft = value;
        }

        setPersistedItem(content:api_content.Content) {
            super.setPersistedItem(content);
            this.persistedDraft = content;

            this.contentWizardHeader.initNames(content.getDisplayName(), content.getName());
            // setup displayName and name to be generated automatically
            // if corresponding values are empty
            this.contentWizardHeader.setAutogenerateName(!content.getName());

            this.formIcon.setSrc(content.getIconUrl());
            var contentData:api_content.ContentData = content.getContentData();

            this.contentForm.renderExisting(contentData, content.getForm());
        }

        persistNewDraft() {

            var contentData = new api_content.ContentData();

            new api_content.CreateContentRequest()
                .setDraft(true)
                .setName("draft")
                .setParent(this.parentContent.getPath())
                .setContentType(this.contentType.getContentTypeName())
                .setDisplayName("")
                .setForm(this.contentForm.getForm())
                .setContentData(contentData)
                .send()
                .done((createResponse:api_rest.JsonResponse<any>) => {

                      var json = createResponse.getJson();

                      if (json.error) {
                          api_notify.showError(json.error.message);
                      } else {
                          api_notify.showFeedback('Content draft was created!');
                          var content:api_content.Content = new api_content.Content(json.result);

                          this.setPersistedDraft(content);
                      }
                });
        }

        persistNewItem(successCallback?:(contentId:string, contentPath:string) => void) {

            var contentData = this.contentForm.getContentData();

            var createRequest = new api_content.CreateContentRequest().
                setName(this.contentWizardHeader.getName()).
                setParent(this.parentContent.getPath()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentForm.getForm()).
                setContentData(contentData);

            if (this.iconUploadId) {
                createRequest.addAttachment(new api_content.Attachment(this.iconUploadId, new api_content.AttachmentName('_thumb.png')));
            }
            var attachments:api_content.Attachment[] = this.contentForm.getFormView().getAttachments();
            createRequest.addAttachments(attachments);

            createRequest.send().done((createResponse:api_rest.JsonResponse<any>) => {

                var json = createResponse.getJson();
                if (json.error) {
                    api_notify.showError(json.error.message);
                } else {
                    api_notify.showFeedback('Content was created!');
                    var content:api_content.Content = new api_content.Content(json.result);
                    new api_content.ContentCreatedEvent(content).fire();
                    this.setPersistedItem(content);
                    this.getTabId().changeToEditMode(content.getId());

                    if (successCallback) {
                        successCallback.call(this, json.contentId, json.contentPath);
                    }
                }
            });
        }

        updatePersistedItem(successCallback?:() => void) {

            var updateRequest = new api_content.UpdateContentRequest(this.getPersistedItem().getId()).
                setContentName(this.contentWizardHeader.getName()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentForm.getForm()).
                setContentData(this.contentForm.getContentData());

            if (this.iconUploadId) {
                updateRequest.addAttachment(new api_content.Attachment(this.iconUploadId, new api_content.AttachmentName('_thumb.png')));
            }

            updateRequest.send().done((updateResponse:api_rest.JsonResponse<any>) => {
                var json = updateResponse.getJson();
                if (json.error) {
                    api_notify.showError(json.error.message);
                } else {
                    api_notify.showFeedback('Content was updated!');
                    var content:api_content.Content = new api_content.Content(json.result);
                    new api_content.ContentUpdatedEvent(content).fire();
                    if (successCallback) {
                        successCallback.call(this, json.contentId, json.contentPath);
                    }
                }

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }

        hasUnsavedChanges():boolean {
            var persistedContent:api_content.Content = this.getPersistedItem();
            if (persistedContent == undefined) {
                return true;
            } else {
                return !this.stringsEqual(persistedContent.getDisplayName(), this.contentWizardHeader.getDisplayName())
                    || !this.stringsEqual(persistedContent.getName(), this.contentWizardHeader.getName())
                    || !persistedContent.getContentData().equals(this.contentForm.getContentData());
            }
        }

        getParentContent():api_content.Content {
            return this.parentContent;
        }

        getContentType():api_schema_content.ContentType {
            return this.contentType;
        }

        private stringsEqual(str1:string, str2:string):boolean {
            // strings are equal if both of them are empty or not specified or they are identical
            return (!str1 && !str2) || (str1 == str2);
        }
    }


}