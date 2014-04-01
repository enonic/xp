module app.wizard {

    export class ContentTypeWizardPanel extends api.app.wizard.WizardPanel<api.schema.content.ContentType> {

        public static NEW_WIZARD_HEADER = "new content type";

        private formIcon: api.app.wizard.FormIcon;

        private contentTypeIcon: api.icon.Icon;

        private contentTypeWizardHeader: api.app.wizard.WizardHeaderWithName;

        private persistedConfig: string;

        private contentTypeForm: app.wizard.ContentTypeForm;

        constructor(tabId: api.app.AppBarTabId, persistedContentType: api.schema.content.ContentType,
                    callback: (wizard: ContentTypeWizardPanel) => void) {
            this.contentTypeWizardHeader = new api.app.wizard.WizardHeaderWithName();
            var defaultFormIconUrl = new api.schema.content.ContentTypeIconUrlResolver().resolveDefault();
            this.formIcon = new api.app.wizard.FormIcon(defaultFormIconUrl, "Click to upload icon",
                api.util.getRestUri("blob/upload"));
            this.formIcon.onUploadFinished((event: api.app.wizard.UploadFinishedEvent) => {
                this.contentTypeIcon = new api.icon.IconBuilder().
                    setBlobKey(event.getUploadItem().getBlobKey()).setMimeType(event.getUploadItem().getMimeType()).build();
                this.formIcon.setSrc(api.util.getRestUri('blob/' + this.contentTypeIcon.getBlobKey()));
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

            var steps: api.app.wizard.WizardStep[] = [];
            steps.push(new api.app.wizard.WizardStep("Content Type", this.contentTypeForm));

            super({
                tabId: tabId,
                persistedItem: persistedContentType,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.contentTypeWizardHeader,
                steps: steps
            }, () => {
                callback(this);
            });
        }

        layoutPersistedItem(persistedContentType: api.schema.content.ContentType): Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.contentTypeWizardHeader.setName(persistedContentType.getName());
            this.formIcon.setSrc(persistedContentType.getIconUrl());

            new api.schema.content.GetContentTypeConfigByNameRequest(persistedContentType.getContentTypeName()).send().
                done((response: api.rest.JsonResponse <api.schema.content.GetContentTypeConfigResult>) => {
                    this.contentTypeForm.setFormData({"xml": response.getResult().contentTypeXml});
                    this.persistedConfig = response.getResult().contentTypeXml || "";
                    deferred.resolve(null);
                });

            return deferred.promise;
        }

        persistNewItem(): Q.Promise<api.schema.content.ContentType> {

            var deferred = Q.defer<api.schema.content.ContentType>();

            var formData = this.contentTypeForm.getFormData();
            var createContentTypeRequest = new api.schema.content.CreateContentTypeRequest(this.contentTypeWizardHeader.getName(),
                formData.xml,
                this.contentTypeIcon);
            createContentTypeRequest.
                sendAndParse().
                done((contentType: api.schema.content.ContentType) => {

                    this.getTabId().changeToEditMode(contentType.getKey());
                    new app.wizard.ContentTypeCreatedEvent().fire();
                    api.notify.showFeedback('Content type was created!');

                    new api.schema.SchemaCreatedEvent(contentType).fire();

                    deferred.resolve(contentType);

                });

            return deferred.promise;
        }

        updatePersistedItem(): Q.Promise<api.schema.content.ContentType> {

            var deferred = Q.defer<api.schema.content.ContentType>();

            var formData = this.contentTypeForm.getFormData();
            var newName = new api.schema.content.ContentTypeName(this.contentTypeWizardHeader.getName());
            var updateContentTypeRequest = new api.schema.content.UpdateContentTypeRequest(this.getPersistedItem().getContentTypeName(),
                newName,
                formData.xml,
                this.contentTypeIcon);

            updateContentTypeRequest.
                sendAndParse().
                then((contentType: api.schema.content.ContentType) => {

                    new app.wizard.ContentTypeUpdatedEvent().fire();
                    api.notify.showFeedback('Content type was saved!');

                    new api.schema.SchemaUpdatedEvent(contentType).fire();

                    deferred.resolve(contentType);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        hasUnsavedChanges(): boolean {
            var persistedContentType: api.schema.content.ContentType = this.getPersistedItem();
            if (persistedContentType == undefined) {
                return true;
            } else {
                return !api.util.isStringsEqual(persistedContentType.getName(), this.contentTypeWizardHeader.getName())
                    || !api.util.isStringsEqual(api.util.removeCarriageChars(this.persistedConfig),
                        api.util.removeCarriageChars(this.contentTypeForm.getFormData().xml));
            }
        }
    }
}