module app_wizard {

    export class ContentWizardPanel extends api_app_wizard.WizardPanel<api_content.Content> {

        private static DEFAULT_CONTENT_ICON_URL:string = api_util.getAdminUri("resources/images/default_content.png");

        private static DISPLAY_NAME_REGEX:RegExp = /\$\('([a-zA-Z\.]*)'\)/g;

        private persistedContent:api_content.Content;

        private parentContent:api_content.Content;

        private renderingNew:boolean;

        private contentType:api_schema_content.ContentType;

        private formIcon:api_app_wizard.FormIcon;

        private contentWizardHeader:api_app_wizard.WizardHeaderWithDisplayNameAndName;

        private contentForm:ContentForm;

        private schemaPanel:api_ui.Panel;

        private modulesPanel:api_ui.Panel;

        private templatesPanel:api_ui.Panel;

        private iconUploadId:string;

        private displayNameScriptExecutor:DisplayNameScriptExecutor;

        constructor(tabId:api_app.AppBarTabId,contentType:api_schema_content.ContentType, parentContent:api_content.Content) {

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

            super({
                tabId: tabId,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                stepToolbar: stepToolbar,
                header: this.contentWizardHeader,
                actions: actions,
                livePanel: livePanel
            });

            this.contentWizardHeader.initNames("New " + this.contentType.getDisplayName(), null);
            this.contentWizardHeader.setAutogenerateName(true);

            console.log("ContentWizardPanel this.contentType: ", this.contentType);
            this.contentForm = new ContentForm();

            this.schemaPanel = new api_ui.Panel("schemaPanel");
            var h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("TODO: schema");
            this.schemaPanel.appendChild(h1El);

            this.modulesPanel = new api_ui.Panel("modulesPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("TODO: modules");
            this.modulesPanel.appendChild(h1El);

            this.templatesPanel = new api_ui.Panel("templatesPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("TODO: templates");
            this.templatesPanel.appendChild(h1El);

            this.addStep(new api_app_wizard.WizardStep(contentType.getDisplayName()), this.contentForm);
            this.addStep(new api_app_wizard.WizardStep("Schemas"), this.schemaPanel);
            this.addStep(new api_app_wizard.WizardStep("Modules"), this.modulesPanel);
            this.addStep(new api_app_wizard.WizardStep("Templates"), this.templatesPanel);

            ShowContentLiveEvent.on((event) => {
                this.toggleFormPanel(false);
            });

            ShowContentFormEvent.on((event) => {
                this.toggleFormPanel(true);
            });

            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            if (contentType.getContentDisplayNameScript()) {
                this.displayNameScriptExecutor.setScript( contentType.getContentDisplayNameScript() );

                this.getEl().addEventListener("keyup", (e) => {

                    this.displayNameScriptExecutor.setFormView( this.contentForm.getFormView() );

                    var displayName = this.displayNameScriptExecutor.execute();

                    this.contentWizardHeader.setDisplayName(displayName);
                });
            }
        }

        showCallback() {
            if(this.persistedContent) {
                app.Router.setHash("edit/" + this.persistedContent.getId());
            } else {
                app.Router.setHash("new/" + this.contentType.getName());
            }
            super.showCallback();
        }

        renderNew() {
            super.renderNew();
            this.contentForm.renderNew(this.contentType.getForm());
            this.renderingNew = true;
        }

        setPersistedItem(content:api_content.Content) {
            super.setPersistedItem(content);
            this.persistedContent = content;
            this.renderingNew = false;

            this.contentWizardHeader.initNames(content.getDisplayName(), content.getName());
            // setup displayName and name to be generated automatically
            // if corresponding values are empty
            this.contentWizardHeader.setAutogenerateName(!content.getName());

            this.formIcon.setSrc(content.getIconUrl());
            var contentData:api_content.ContentData = content.getContentData();

            this.contentForm.renderExisting(contentData, content.getForm());
        }

        persistNewItem(successCallback?:(contentId:string, contentPath:string) => void) {

            var contentData = this.contentForm.getContentData();

            var createRequest = new api_content.CreateContentRequest().
                setContentName(this.contentWizardHeader.getName()).
                setParentContentPath(this.parentContent.getPath().toString()).
                setContentType(this.contentType.getName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentForm.getForm()).
                setContentData(contentData);

                if(this.iconUploadId) {
                    createRequest.addAttachment( new api_content.Attachment(this.iconUploadId, new api_content.AttachmentName(null, '_thumb.png' )));
                }
                var attachments:api_content.Attachment[] = this.contentForm.getFormView().getAttachments();
                createRequest.addAttachments( attachments );

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

            var updateRequest = new api_content.UpdateContentRequest(this.persistedContent.getId()).
                setContentName(this.contentWizardHeader.getName()).
                setContentType(this.contentType.getName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentForm.getForm()).
                setContentData(this.contentForm.getContentData());

            if(this.iconUploadId) {
                updateRequest.setAttachments([
                    {
                        uploadId: this.iconUploadId,
                        attachmentName: '_thumb.png'
                    }
                ])
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
            if (this.persistedContent == undefined) {
                return true;
            } else {
                return !this.stringsEqual(this.persistedContent.getDisplayName(), this.contentWizardHeader.getDisplayName())
                    || !this.stringsEqual(this.persistedContent.getName(), this.contentWizardHeader.getName())
                    || !this.persistedContent.getContentData().equals(this.contentForm.getContentData());
            }
        }

        private stringsEqual(str1:string, str2:string):boolean {
            // strings are equal if both of them are empty or not specified or they are identical
            return (!str1 && !str2) || (str1 == str2);
        }
    }

    class LiveFormPanel extends api_ui.Panel {

        private frame:api_dom.IFrameEl;

        constructor(url:string = api_util.getUri("dev/live-edit-page/bootstrap.jsp?edit=true")) {
            super("LiveFormPanel");
            this.addClass("live-form-panel");

            this.frame = new api_dom.IFrameEl();
            this.frame.addClass("live-edit-frame");
            this.frame.setSrc(url);
            this.appendChild(this.frame);

            // Wait for iframe to be loaded before adding context window!
            var intervalId = setInterval(() => {
                if (this.frame.isLoaded()) {
                    var contextWindow = new app_contextwindow.ContextWindow({liveEditEl: this.frame});
                    this.appendChild(contextWindow);
                    clearInterval(intervalId);
                }
            }, 200);

        }

    }
}