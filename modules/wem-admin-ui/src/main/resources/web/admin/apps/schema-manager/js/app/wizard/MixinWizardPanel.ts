module app.wizard {

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;

    export class MixinWizardPanel extends api.app.wizard.WizardPanel<api.schema.mixin.Mixin> {

        public static NEW_WIZARD_HEADER = "New Mixin";

        private formIcon: api.app.wizard.FormIcon;

        private mixinIcon: api.icon.Icon;

        private mixinWizardHeader: api.app.wizard.WizardHeaderWithName;

        private persistedConfig: string;

        private mixinForm: MixinForm;

        /**
         * Whether constructor is being currently executed or not.
         */
        private constructing: boolean;

        constructor(tabId: api.app.AppBarTabId, persistedMixin: api.schema.mixin.Mixin, callback: (wizard: MixinWizardPanel) => void) {

            this.constructing = true;
            this.mixinWizardHeader = new api.app.wizard.WizardHeaderWithName();
            this.formIcon = new api.app.wizard.FormIcon(new api.schema.mixin.MixinIconUrlResolver().resolveDefault(),
                "Click to upload icon", api.util.getRestUri("blob/upload"));

            this.formIcon.onUploadFinished((event: api.app.wizard.UploadFinishedEvent) => {
                this.mixinIcon = new api.icon.IconBuilder().
                    setBlobKey(event.getUploadItem().getBlobKey()).setMimeType(event.getUploadItem().getMimeType()).build();
                this.formIcon.setSrc(api.util.getRestUri('blob/' + this.mixinIcon.getBlobKey() + '?mimeType=image/png'));
            });
            var actions = new MixinWizardActions(this);

            var mainToolbar = new MixinWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            this.mixinWizardHeader.setName(MixinWizardPanel.NEW_WIZARD_HEADER);

            this.mixinForm = new MixinForm();

            var steps: api.app.wizard.WizardStep[] = [];
            steps.push(new api.app.wizard.WizardStep("Mixin", this.mixinForm));

            super({
                tabId: tabId,
                persistedItem: persistedMixin,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.mixinWizardHeader,
                steps: steps
            }, () => {

                this.constructing = false;
                callback(this);
            });
        }

        layoutPersistedItem(persistedMixin: api.schema.mixin.Mixin): Q.Promise<void> {

            this.formIcon.setSrc(persistedMixin.getIconUrl() + '?crop=false');

            if (!this.constructing) {

                var deferred = Q.defer<void>();

                var viewedMixinBuilder = new api.schema.mixin.MixinBuilder(persistedMixin);
                viewedMixinBuilder.setName(this.mixinWizardHeader.getName());
                var viewedItem = viewedMixinBuilder.build();
                if (viewedItem.equals(persistedMixin)) {

                    // Do nothing, since viewed data equals persisted data
                    deferred.resolve(null);
                    return deferred.promise;
                }
                else {
                    ConfirmationDialog.get().
                        setQuestion("Received Mixin from server differs from what you have. Would you like to load changes from server?").
                        setYesCallback(() => {

                            this.doLayoutPersistedItem(persistedMixin);
                        }).
                        setNoCallback(() => {
                            // Do nothing...
                        }).show();

                    deferred.resolve(null);
                    return deferred.promise;
                }
            }
            else {
                return this.doLayoutPersistedItem(persistedMixin);
            }
        }

        doLayoutPersistedItem(persistedMixin: api.schema.mixin.Mixin): Q.Promise<void> {

            this.mixinWizardHeader.setName(persistedMixin.getName());

            return new api.schema.mixin.GetMixinConfigByNameRequest(persistedMixin.getMixinName()).
                send().
                then((response: api.rest.JsonResponse<api.schema.mixin.GetMixinConfigResult>): void => {

                    this.mixinForm.render();
                    this.mixinForm.setFormData({"xml": response.getResult().mixinXml});
                    this.persistedConfig = response.getResult().mixinXml || "";

                });
        }

        saveChanges(): Q.Promise<api.schema.mixin.Mixin> {
            var formData = this.mixinForm.getFormData();
            this.persistedConfig = formData.xml;
            return super.saveChanges();
        }

        persistNewItem(): Q.Promise<api.schema.mixin.Mixin> {

            var formData = this.mixinForm.getFormData();

            var createRequest = new api.schema.mixin.CreateMixinRequest().
                setName(this.mixinWizardHeader.getName()).
                setConfig(formData.xml).
                setIcon(this.mixinIcon);

            return createRequest.
                sendAndParse().
                then((mixin: api.schema.mixin.Mixin) => {

                    this.getTabId().changeToEditMode(mixin.getKey());
                    api.notify.showFeedback('Mixin was created!');

                    new api.schema.SchemaCreatedEvent(mixin).fire();

                    return mixin;
                });
        }

        updatePersistedItem(): Q.Promise<api.schema.mixin.Mixin> {

            var formData = this.mixinForm.getFormData();

            var updateRequest = new api.schema.mixin.UpdateMixinRequest().
                setMixinToUpdate(this.getPersistedItem().getName()).
                setName(this.mixinWizardHeader.getName()).
                setConfig(formData.xml).
                setIcon(this.mixinIcon);

            return updateRequest.
                sendAndParse().
                then((mixin: api.schema.mixin.Mixin) => {

                    api.notify.showFeedback('Mixin was updated!');
                    new api.schema.SchemaUpdatedEvent(mixin).fire();

                    return mixin;
                });
        }

        hasUnsavedChanges(): boolean {
            var persistedMixin: api.schema.mixin.Mixin = this.getPersistedItem();
            if (persistedMixin == undefined) {
                return true;
            } else {
                return !api.util.isStringsEqual(persistedMixin.getName(), this.mixinWizardHeader.getName())
                    || !api.util.isStringsEqual(api.util.removeCarriageChars(this.persistedConfig),
                        api.util.removeCarriageChars(this.mixinForm.getFormData().xml));
            }
        }
    }
}