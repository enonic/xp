module app_wizard {

    export class ContentTypeWizardPanel extends api_app_wizard.WizardPanel {

        public static NEW_WIZARD_HEADER = "New Content Type";

        private static DEFAULT_CHEMA_ICON_URL:string = '/admin/rest/schema/image/ContentType:system:structured';

        private saveAction:api_ui.Action;

        private closeAction:api_ui.Action;

        private formIcon:api_app_wizard.FormIcon;

        private toolbar:api_ui_toolbar.Toolbar;

        private header:api_app_wizard.WizardHeaderWithName;

        private persistedContentType:api_remote_contenttype.ContentType;

        private contentTypeForm:app_wizard.ContentTypeForm;

        constructor(id:string) {

            this.header = new api_app_wizard.WizardHeaderWithName();
            this.formIcon =
            new api_app_wizard.FormIcon(ContentTypeWizardPanel.DEFAULT_CHEMA_ICON_URL, "Click to upload icon", "rest/upload");

            this.closeAction = new api_app_wizard.CloseAction(this);
            this.saveAction = new api_app_wizard.SaveAction(this);

            this.toolbar = new ContentTypeWizardToolbar({
                saveAction: this.saveAction,
                closeAction: this.closeAction
            });

            super({
                formIcon: this.formIcon,
                toolbar: this.toolbar,
                header: this.header
            });

            this.header.setName(ContentTypeWizardPanel.NEW_WIZARD_HEADER);

            this.contentTypeForm = new ContentTypeForm();

            this.addStep(new api_app_wizard.WizardStep("Content Type", this.contentTypeForm));


        }

        setPersistedItem(contentType:api_remote_contenttype.ContentType) {
            super.setPersistedItem(contentType);

            this.header.setName(contentType.name);
            this.formIcon.setSrc(contentType.iconUrl);

            this.persistedContentType = contentType;

            var contentTypeGetParams:api_remote_contenttype.GetParams = {
                qualifiedNames: [contentType.qualifiedName],
                format: 'XML'
            };

            api_remote_contenttype.RemoteContentTypeService.contentType_get(contentTypeGetParams, (result:api_remote_contenttype.GetResult) => {
                this.contentTypeForm.setFormData({"xml":result.contentTypeXmls[0]});
            })
        }

        persistNewItem(successCallback?:() => void) {
            var formData = this.contentTypeForm.getFormData();
            var createParams:api_remote_contenttype.CreateOrUpdateParams = {
                name: this.header.getName(),
                contentType: formData.xml,
                iconReference: this.getIconUrl()
            };

            api_remote_contenttype.RemoteContentTypeService.contentType_createOrUpdate(createParams,
                (result:api_remote_contenttype.CreateOrUpdateResult) => {
                    if (result.created) {
                        new app_wizard.ContentTypeCreatedEvent().fire();
                        api_notify.showFeedback('Content type was created!');
                    } else {
                        api_notify.newError(result.failure).send();
                    }
                });
        }

        updatePersistedItem(successCallback?:() => void) {
            var formData = this.contentTypeForm.getFormData();
            var updateParams:api_remote_contenttype.CreateOrUpdateParams = {
                name: this.header.getName(),
                contentType: formData.xml,
                iconReference: this.getIconUrl()
            };

            api_remote_contenttype.RemoteContentTypeService.contentType_createOrUpdate(updateParams,
                (result:api_remote_contenttype.CreateOrUpdateResult) => {
                    if (result.updated) {
                        new app_wizard.ContentTypeUpdatedEvent().fire();
                        api_notify.showFeedback('Content type was saved!');
                    } else {
                        api_notify.newError(result.failure).send();
                    }
                });
        }
    }
}