module app_wizard {

    export class ContentTypeWizardPanel extends api_app_wizard.WizardPanel<api_schema_content.ContentType> {

        public static NEW_WIZARD_HEADER = "New Content Type";

        private formIcon: api_app_wizard.FormIcon;

        private contentTypeIcon: api_icon.Icon;

        private contentTypeWizardHeader: api_app_wizard.WizardHeaderWithName;

        private persistedContentType: api_schema_content.ContentType;

        private contentTypeForm: app_wizard.ContentTypeForm;

        constructor(tabId: api_app.AppBarTabId, persistedContentType: api_schema_content.ContentType) {
            this.contentTypeWizardHeader = new api_app_wizard.WizardHeaderWithName();
            var defaultFormIconUrl = new api_schema_content.ContentTypeIconUrlResolver().resolveDefault();
            this.formIcon = new api_app_wizard.FormIcon(defaultFormIconUrl, "Click to upload icon",
                api_util.getRestUri("blob/upload"));
            this.formIcon.addListener({
                onUploadStarted: null,
                onUploadFinished: (uploadItem: api_ui.UploadItem) => {
                    this.contentTypeIcon = new api_icon.IconBuilder().
                        setBlobKey(uploadItem.getBlobKey()).setMimeType(uploadItem.getMimeType()).build();
                    this.formIcon.setSrc(api_util.getRestUri('blob/' + this.contentTypeIcon.getBlobKey()));
                }
            });
            var actions = new ContentTypeWizardActions(this);

            var mainToolbar = new ContentTypeWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            this.contentTypeWizardHeader.setName(ContentTypeWizardPanel.NEW_WIZARD_HEADER);

            this.contentTypeForm = new ContentTypeForm();

            var steps: api_app_wizard.WizardStep[] = [];
            steps.push(new api_app_wizard.WizardStep("Content Type", this.contentTypeForm));

            super({
                tabId: tabId,
                persistedItem:persistedContentType,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.contentTypeWizardHeader,
                steps: steps
            });
        }

        setPersistedItem(contentType: api_schema_content.ContentType) {
            super.setPersistedItem(contentType);

            this.contentTypeWizardHeader.setName(contentType.getName());
            this.formIcon.setSrc(contentType.getIconUrl());

            this.persistedContentType = contentType;

            new api_schema_content.GetContentTypeConfigByNameRequest(contentType.getContentTypeName()).send().
                done((response: api_rest.JsonResponse <api_schema_content.GetContentTypeConfigResult>) => {
                    this.contentTypeForm.setFormData({"xml": response.getResult().contentTypeXml});
                });
        }

        persistNewItem(successCallback ?: () => void) {
            var formData = this.contentTypeForm.getFormData();
            var createContentTypeRequest = new api_schema_content.CreateContentTypeRequest(this.contentTypeWizardHeader.getName(),
                formData.xml,
                this.contentTypeIcon);
            createContentTypeRequest.
                sendAndParse().
                done((contentType: api_schema_content.ContentType) => {

                    this.setPersistedItem(contentType);
                    this.getTabId().changeToEditMode(contentType.getKey());
                    new app_wizard.ContentTypeCreatedEvent().fire();
                    api_notify.showFeedback('Content type was created!');

                    new api_schema.SchemaCreatedEvent(contentType).fire();

                    if (successCallback) {
                        successCallback.call(this);
                    }

                });
        }

        updatePersistedItem(successCallback ?: () => void) {

            var formData = this.contentTypeForm.getFormData();
            var newName = new api_schema_content.ContentTypeName(this.contentTypeWizardHeader.getName());
            var updateContentTypeRequest = new api_schema_content.UpdateContentTypeRequest(this.persistedContentType.getContentTypeName(),
                newName,
                formData.xml,
                this.contentTypeIcon);

            updateContentTypeRequest.
                sendAndParse().
                done((contentType: api_schema_content.ContentType) => {

                    new app_wizard.ContentTypeUpdatedEvent().fire();
                    api_notify.showFeedback('Content type was saved!');

                    new api_schema.SchemaUpdatedEvent(contentType).fire();
                    this.setPersistedItem(contentType);
                    if (successCallback) {
                        successCallback.call(this);
                    }
                });
        }
    }
}