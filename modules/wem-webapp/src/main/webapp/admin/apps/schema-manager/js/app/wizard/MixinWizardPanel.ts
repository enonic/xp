module app_wizard {

    export class MixinWizardPanel extends api_app_wizard.WizardPanel<api_schema_mixin.Mixin> {

        public static NEW_WIZARD_HEADER = "New Mixin";

        private static DEFAULT_CHEMA_ICON_URL:string = api_util.getRestUri('schema/image/Mixin:_');

        private formIcon:api_app_wizard.FormIcon;

        private mixinWizardHeader:api_app_wizard.WizardHeaderWithName;

        private persistedMixin:api_schema_mixin.Mixin;

        private mixinForm:MixinForm;

        constructor(tabId:api_app.AppBarTabId) {

            this.mixinWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon =
            new api_app_wizard.FormIcon(MixinWizardPanel.DEFAULT_CHEMA_ICON_URL, "Click to upload icon",
                api_util.getRestUri("upload"));

            var actions = new MixinWizardActions(this);

            var mainToolbar = new MixinWizardToolbar({
                 saveAction: actions.getSaveAction(),
                 duplicateAction: actions.getDuplicateAction(),
                 deleteAction: actions.getDeleteAction(),
                 closeAction: actions.getCloseAction()
            });

            super({
                tabId: tabId,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.mixinWizardHeader
            });

            this.mixinWizardHeader.setName(MixinWizardPanel.NEW_WIZARD_HEADER);

            this.mixinForm = new MixinForm();
            this.addStep(new api_app_wizard.WizardStep("Mixin"), this.mixinForm);
        }

        setPersistedItem(mixin:api_schema_mixin.Mixin) {
            super.setPersistedItem(mixin);

            this.mixinWizardHeader.setName(mixin.getName());
            this.formIcon.setSrc(mixin.getIcon());

            this.persistedMixin = mixin;

            new api_schema_mixin.GetMixinConfigByQualifiedNameRequest(mixin.getName()).send().
                done((response:api_rest.JsonResponse<api_schema_mixin.GetMixinConfigResult>) => {
                this.mixinForm.setFormData({"xml": response.getResult().mixinXml});
            });

        }

        persistNewItem( successCallback?:() => void ) {
            var formData = this.mixinForm.getFormData();

            var createRequest = new api_schema_mixin.CreateMixinRequest().
                setName( this.mixinWizardHeader.getName() ).
                setConfig( formData.xml ).
                setIconReference( this.getIconUrl() );

            createRequest.send().done( ( response:api_rest.JsonResponse<api_schema_mixin_json.MixinJson> ) => {
                var jsonResponse = response.getJson();
                if (jsonResponse.error) {
                    api_notify.showError(jsonResponse.error.msg);
                } else {
                    var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(jsonResponse.getResult());
                    this.setPersistedItem(mixin);
                    this.getTabId().changeToEditMode(mixin.getKey());
                    api_notify.showFeedback( 'Mixin was created!' );

                    new api_schema.SchemaCreatedEvent( mixin ).fire();

                    if ( successCallback )
                    {
                        successCallback.call( this );
                    }
                }
               } );
        }

        updatePersistedItem( successCallback?:() => void ) {
            var formData = this.mixinForm.getFormData();

            var updateRequest = new api_schema_mixin.UpdateMixinRequest().
                setMixinToUpdate( this.persistedMixin.getName() ).
                setName( this.mixinWizardHeader.getName() ).
                setConfig( formData.xml ).
                setIconReference( this.getIconUrl() );

            updateRequest.send().done( ( response:api_rest.JsonResponse<any> ) => {
                var jsonResponse = response.getJson();
                if (jsonResponse.error) {
                    api_notify.showError(jsonResponse.error.msg);
                } else {
                    var mixin:api_schema_mixin.Mixin = new api_schema_mixin.Mixin(jsonResponse.result);
                    api_notify.showFeedback( 'Mixin was updated!' );

                    new api_schema.SchemaUpdatedEvent( mixin ).fire();

                    if ( successCallback )
                    {
                        successCallback.call( this );
                    }
                }
               } );
        }
    }
}