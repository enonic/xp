module app_wizard {

    export class ContentTypeWizardPanel extends api_app_wizard.WizardPanel<api_schema_content.ContentType> {

        public static NEW_WIZARD_HEADER = "New Content Type";

        private static  DEFAULT_SCHEMA_ICON_URL:string = api_util.getRestUri('schema/image/ContentType:structured');

        private formIcon :api_app_wizard.FormIcon;

        private contentTypeWizardHeader :api_app_wizard.WizardHeaderWithName;

        private persistedContentType :api_schema_content.ContentType;

        private contentTypeForm :app_wizard.ContentTypeForm;

        constructor(tabId:api_app.AppBarTabId) {
            this.contentTypeWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon = new api_app_wizard.FormIcon(ContentTypeWizardPanel.DEFAULT_SCHEMA_ICON_URL, "Click to upload icon",
                api_util.getRestUri("upload"));

            var actions = new ContentTypeWizardActions(this);

            var mainToolbar = new ContentTypeWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            this.contentTypeWizardHeader.setName(ContentTypeWizardPanel.NEW_WIZARD_HEADER);

            this.contentTypeForm = new ContentTypeForm();

            var steps:api_app_wizard.WizardStep[] = [];
            steps.push(new api_app_wizard.WizardStep("Content Type", this.contentTypeForm));

            super({
                tabId: tabId,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.contentTypeWizardHeader,
                steps: steps
            });
        }

        setPersistedItem(contentType:api_schema_content.ContentType) {
            super.setPersistedItem(contentType);

            this.contentTypeWizardHeader.setName(contentType.getName());
            this.formIcon.setSrc(contentType.getIcon());

            this.persistedContentType = contentType;

            new api_schema_content.GetContentTypeConfigByQualifiedNameRequest(contentType.getName()).send().
                done((response:api_rest.JsonResponse <api_schema_content.GetContentTypeConfigResult>) => {
                this.contentTypeForm.setFormData({"xml": response.getResult().contentTypeXml});
            });
        }

        persistNewItem(successCallback ? : () => void) {
            var formData = this.contentTypeForm.getFormData();
            var createContentTypeRequest = new api_schema_content.CreateContentTypeRequest(this.contentTypeWizardHeader.getName(), formData.xml,
                this.getIconUrl());
            createContentTypeRequest.send().done((response:api_rest.JsonResponse<any>) => {
                var jsonResponse = response.getJson();
                if (jsonResponse.error) {
                    api_notify.showError(jsonResponse.error.msg);
                } else {
                    var contentType:api_schema_content.ContentType = new api_schema_content.ContentType(jsonResponse.result);
                    this.setPersistedItem(contentType);
                    this.getTabId().changeToEditMode(contentType.getKey());
                    new app_wizard.ContentTypeCreatedEvent().fire();
                    api_notify.showFeedback('Content type was created!');

                    new api_schema.SchemaCreatedEvent( contentType ).fire();

                    if (successCallback) {
                        successCallback.call(this);
                    }
                }

            });
        }

        updatePersistedItem(successCallback ? : () => void) {
            var formData = this.contentTypeForm.getFormData();
            var updateContentTypeRequest = new api_schema_content.UpdateContentTypeRequest(this.contentTypeWizardHeader.getName(), formData.xml,
                this.getIconUrl());

            updateContentTypeRequest.send().done((response:api_rest.JsonResponse<any>) => {
                var jsonResponse = response.getJson();
                if (jsonResponse.error) {
                    api_notify.showError(jsonResponse.error.msg);
                } else {
                    var contentType:api_schema_content.ContentType = new api_schema_content.ContentType(jsonResponse.result);
                    new app_wizard.ContentTypeUpdatedEvent().fire();
                    api_notify.showFeedback('Content type was saved!');

                    new api_schema.SchemaUpdatedEvent( contentType ).fire();

                    if (successCallback) {
                        successCallback.call(this);
                    }
                }
            });
        }
    }
}