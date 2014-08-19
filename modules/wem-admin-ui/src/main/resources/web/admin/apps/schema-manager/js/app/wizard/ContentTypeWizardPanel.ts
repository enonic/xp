module app.wizard {

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import ContentTypeIconUrlResolver = api.schema.content.ContentTypeIconUrlResolver;

    export class ContentTypeWizardPanel extends api.app.wizard.WizardPanel<api.schema.content.ContentType> {

        public static NEW_WIZARD_HEADER = "New Content Type";

        private formIcon: api.app.wizard.FormIcon;

        private contentTypeIcon: api.icon.Icon;

        private contentTypeWizardHeader: api.app.wizard.WizardHeaderWithName;

        private persistedConfig: string;

        private contentTypeForm: app.wizard.ContentTypeForm;

        /**
         * Whether constructor is being currently executed or not.
         */
        private constructing: boolean;

        constructor(tabId: api.app.AppBarTabId, persistedContentType: api.schema.content.ContentType,
                    callback: (wizard: ContentTypeWizardPanel) => void) {
            this.constructing = true;
            this.contentTypeWizardHeader = new api.app.wizard.WizardHeaderWithName();
            var defaultFormIconUrl = ContentTypeIconUrlResolver.default();
            this.formIcon = new api.app.wizard.FormIcon(defaultFormIconUrl, "Click to upload icon",
                api.util.getRestUri("blob/upload"));
            this.formIcon.onUploadFinished((event: api.app.wizard.UploadFinishedEvent) => {
                this.contentTypeIcon = new api.icon.IconBuilder().
                    setBlobKey(event.getUploadItem().getBlobKey()).setMimeType(event.getUploadItem().getMimeType()).build();
                this.formIcon.setSrc(api.util.getRestUri('blob/' + this.contentTypeIcon.getBlobKey() + '?mimeType=' +
                                                         event.getUploadItem().getMimeType()));
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
                this.constructing = false;
                callback(this);
            });
        }

        layoutPersistedItem(persistedContentType: api.schema.content.ContentType): Q.Promise<void> {

            this.formIcon.setSrc(persistedContentType.getIconUrl() + '?crop=false');

            if (!this.constructing) {

                var deferred = Q.defer<void>();

                var viewedItemBuilder = new api.schema.content.ContentTypeBuilder(persistedContentType);
                viewedItemBuilder.setName(this.contentTypeWizardHeader.getName());
                var viewedItem = viewedItemBuilder.build();
                if (viewedItem.equals(persistedContentType)) {

                    // Do nothing
                    deferred.resolve(null);
                    return deferred.promise;
                }
                else {
                    ConfirmationDialog.get().
                        setQuestion("Received ContentType from server differs from what you have. Would you like to load changes from server?").
                        setYesCallback(() => {

                            this.doLayoutPersistedItem(persistedContentType);
                        }).
                        setNoCallback(() => {
                            // Do nothing...
                        }).show();

                    deferred.resolve(null);
                    return deferred.promise;
                }
            }
            else {
                return this.doLayoutPersistedItem(persistedContentType);
            }
        }

        private doLayoutPersistedItem(persistedContentType: api.schema.content.ContentType): Q.Promise<void> {

            this.contentTypeWizardHeader.setName(persistedContentType.getName());

            return new api.schema.content.GetContentTypeConfigByNameRequest(persistedContentType.getContentTypeName()).send().
                then((response: api.rest.JsonResponse <api.schema.content.GetContentTypeConfigResult>): void => {
                    this.contentTypeForm.setFormData({"xml": response.getResult().contentTypeXml});
                    this.persistedConfig = response.getResult().contentTypeXml || "";
                });
        }

        saveChanges(): Q.Promise<api.schema.content.ContentType> {
            var formData = this.contentTypeForm.getFormData();
            this.persistedConfig = formData.xml;
            return super.saveChanges();
        }

        persistNewItem(): Q.Promise<api.schema.content.ContentType> {

            var formData = this.contentTypeForm.getFormData();
            var createContentTypeRequest = new api.schema.content.CreateContentTypeRequest(this.contentTypeWizardHeader.getName(),
                formData.xml,
                this.contentTypeIcon);
            return createContentTypeRequest.
                sendAndParse().
                then((contentType: api.schema.content.ContentType) => {

                    this.getTabId().changeToEditMode(contentType.getKey());
                    new app.wizard.ContentTypeCreatedEvent().fire();
                    api.notify.showFeedback('Content type was created!');

                    new api.schema.SchemaCreatedEvent(contentType).fire();

                    return contentType;

                });
        }

        updatePersistedItem(): Q.Promise<api.schema.content.ContentType> {

            var formData = this.contentTypeForm.getFormData();
            var newName = new api.schema.content.ContentTypeName(this.contentTypeWizardHeader.getName());
            var updateContentTypeRequest = new api.schema.content.UpdateContentTypeRequest(this.getPersistedItem().getContentTypeName(),
                newName,
                formData.xml,
                this.contentTypeIcon);

            return updateContentTypeRequest.
                sendAndParse().
                then((contentType: api.schema.content.ContentType) => {

                    new app.wizard.ContentTypeUpdatedEvent().fire();
                    api.notify.showFeedback('Content type was saved!');

                    new api.schema.SchemaUpdatedEvent(contentType).fire();

                    return contentType;

                });
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