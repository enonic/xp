module app_wizard {

    export class MixinWizardPanel extends api_app_wizard.WizardPanel {

        public static NEW_WIZARD_HEADER = "New Mixin";

        private static DEFAULT_CHEMA_ICON_URL:string = '/admin/rest/schema/image/Mixin:_:_';

        private saveAction:api_ui.Action;

        private closeAction:api_ui.Action;

        private formIcon:api_app_wizard.FormIcon;

        private mixinWizardHeader:api_app_wizard.WizardHeaderWithName;

        private persistedMixin:api_remote_mixin.Mixin;

        private mixinForm:MixinForm;

        constructor(id:string) {

            this.mixinWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon = new api_app_wizard.FormIcon(MixinWizardPanel.DEFAULT_CHEMA_ICON_URL, "Click to upload icon", "rest/upload");

            this.closeAction = new api_app_wizard.CloseAction(this);
            this.saveAction = new api_app_wizard.SaveAction(this);

            var toolbar = new MixinWizardToolbar({
                saveAction: this.saveAction,
                closeAction: this.closeAction
            });

            super({
                formIcon: this.formIcon,
                toolbar: toolbar,
                header: this.mixinWizardHeader
            });

            this.mixinWizardHeader.setName(MixinWizardPanel.NEW_WIZARD_HEADER);

            this.mixinForm = new MixinForm();
            this.addStep(new api_app_wizard.WizardStep("Mixin", this.mixinForm));
        }

        setPersistedItem(mixin:api_remote_mixin.Mixin) {
            super.setPersistedItem(mixin);

            this.mixinWizardHeader.setName(mixin.name);
            this.formIcon.setSrc(mixin.iconUrl);

            this.persistedMixin = mixin;

            var mixinGetParams:api_remote_mixin.GetParams = {
                qualifiedName: mixin.module + ":" + mixin.name,
                format: 'XML'
            };

            api_remote_mixin.RemoteMixinService.mixin_get(mixinGetParams, (result:api_remote_mixin.GetResult) => {
                this.mixinForm.setFormData({"xml": result.mixinXml})
            });
        }

        persistNewItem(successCallback?:() => void) {
            var formData = this.mixinForm.getFormData();
            var createParams:api_remote_mixin.CreateOrUpdateParams = {
                name: this.mixinWizardHeader.getName(),
                mixin: formData.xml,
                iconReference: this.getIconUrl()
            };

            api_remote_mixin.RemoteMixinService.mixin_createOrUpdate(createParams, () => {
                new app_wizard.MixinCreatedEvent().fire();
                api_notify.showFeedback('Mixin was created!');

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }

        updatePersistedItem(successCallback?:() => void) {
            var formData = this.mixinForm.getFormData();
            var updateParams:api_remote_mixin.CreateOrUpdateParams = {
                name: this.mixinWizardHeader.getName(),
                mixin: formData.xml,
                iconReference: this.getIconUrl()
            };

            api_remote_mixin.RemoteMixinService.mixin_createOrUpdate(updateParams, () => {
                new app_wizard.MixinUpdatedEvent().fire();
                api_notify.showFeedback('Mixin was saved!');

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }
    }
}