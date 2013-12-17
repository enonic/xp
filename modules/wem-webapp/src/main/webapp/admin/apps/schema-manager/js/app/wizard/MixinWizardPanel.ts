module app_wizard {

    export class MixinWizardPanel extends api_app_wizard.WizardPanel<api_schema_mixin.Mixin> {

        public static NEW_WIZARD_HEADER = "New Mixin";

        private formIcon: api_app_wizard.FormIcon;

        private mixinIcon: api_icon.Icon;

        private mixinWizardHeader: api_app_wizard.WizardHeaderWithName;

        private persistedMixin: api_schema_mixin.Mixin;

        private mixinForm: MixinForm;

        constructor(tabId: api_app.AppBarTabId, persistedMixin:api_schema_mixin.Mixin, callback: (wizard:MixinWizardPanel) => void) {

            this.mixinWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon = new api_app_wizard.FormIcon(new api_schema_mixin.MixinIconUrlResolver().resolveDefault(),
                "Click to upload icon", api_util.getRestUri("blob/upload"));

            this.formIcon.addListener({
                onUploadStarted: null,
                onUploadFinished: (uploadItem: api_ui.UploadItem) => {
                    this.mixinIcon = new api_icon.IconBuilder().
                        setBlobKey(uploadItem.getBlobKey()).setMimeType(uploadItem.getMimeType()).build();
                    this.formIcon.setSrc(api_util.getRestUri('blob/' + this.mixinIcon.getBlobKey()));
                }
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

            var steps: api_app_wizard.WizardStep[] = [];
            steps.push(new api_app_wizard.WizardStep("Mixin", this.mixinForm));

            super({
                tabId: tabId,
                persistedItem:persistedMixin,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.mixinWizardHeader,
                steps: steps
            }, () => {
                callback(this);
            });
        }

        initWizardPanel() {
            super.initWizardPanel();
            this.mixinForm.reRender();
        }

        setPersistedItem(mixin: api_schema_mixin.Mixin, callback:Function) {

            super.setPersistedItem(mixin, () => {
                this.mixinWizardHeader.setName(mixin.getName());
                this.formIcon.setSrc(mixin.getIconUrl());

                this.persistedMixin = mixin;

                new api_schema_mixin.GetMixinConfigByNameRequest(mixin.getMixinName()).send().
                    done((response: api_rest.JsonResponse<api_schema_mixin.GetMixinConfigResult>) => {
                        this.mixinForm.reRender();
                        this.mixinForm.setFormData({"xml": response.getResult().mixinXml});
                        callback();
                    });
            });

        }

        persistNewItem(successCallback?: () => void) {
            var formData = this.mixinForm.getFormData();

            var createRequest = new api_schema_mixin.CreateMixinRequest().
                setName(this.mixinWizardHeader.getName()).
                setConfig(formData.xml).
                setIcon(this.mixinIcon);

            createRequest.
                sendAndParse().
                done((mixin: api_schema_mixin.Mixin) => {

                    this.setPersistedItem(mixin, () => {
                        this.getTabId().changeToEditMode(mixin.getKey());
                        api_notify.showFeedback('Mixin was created!');

                        new api_schema.SchemaCreatedEvent(mixin).fire();

                        if (successCallback) {
                            successCallback.call(this);
                        }
                    });
                });
        }

        updatePersistedItem(successCallback?: () => void) {
            var formData = this.mixinForm.getFormData();

            var updateRequest = new api_schema_mixin.UpdateMixinRequest().
                setMixinToUpdate(this.persistedMixin.getName()).
                setName(this.mixinWizardHeader.getName()).
                setConfig(formData.xml).
                setIcon(this.mixinIcon);

            updateRequest.
                sendAndParse().
                done((mixin: api_schema_mixin.Mixin) => {

                    api_notify.showFeedback('Mixin was updated!');
                    this.setPersistedItem(mixin, () => {
                        new api_schema.SchemaUpdatedEvent(mixin).fire();

                        if (successCallback) {
                            successCallback.call(this);
                        }
                    });
                });
        }
    }
}