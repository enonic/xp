module app_wizard {

    export class RelationshipTypeWizardPanel extends api_app_wizard.WizardPanel {

        public static NEW_WIZARD_HEADER = "New Relationship Type";

        private static DEFAULT_CHEMA_ICON_URL:string = api_util.getRestUri('schema/image/RelationshipType:_');

        private saveAction:api_ui.Action;

        private closeAction:api_ui.Action;

        private formIcon:api_app_wizard.FormIcon;


        private relationShipTypeWizardHeader:api_app_wizard.WizardHeaderWithName;

        private persistedRelationshipType:api_schema_relationshiptype.RelationshipType;

        private relationshipTypeForm:RelationshipTypeForm;

        constructor() {

            this.relationShipTypeWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon =
            new api_app_wizard.FormIcon(RelationshipTypeWizardPanel.DEFAULT_CHEMA_ICON_URL, "Click to upload icon",
                api_util.getRestUri("upload"));

            this.closeAction = new api_app_wizard.CloseAction(this);
            this.saveAction = new api_app_wizard.SaveAction(this);

            var toolbar = new RelationshipTypeWizardToolbar({
                saveAction: this.saveAction,
                closeAction: this.closeAction
            });

            super({
                formIcon: this.formIcon,
                toolbar: toolbar,
                header: this.relationShipTypeWizardHeader
            });

            this.relationShipTypeWizardHeader.setName(RelationshipTypeWizardPanel.NEW_WIZARD_HEADER);
            this.relationshipTypeForm = new RelationshipTypeForm();
            this.addStep(new api_app_wizard.WizardStep("Relationship Type"), this.relationshipTypeForm);
        }

        setPersistedItem(relationshipType:api_schema_relationshiptype.RelationshipType) {
            super.setPersistedItem(relationshipType);

            this.relationShipTypeWizardHeader.setName(relationshipType.getName());
            this.formIcon.setSrc(relationshipType.getIcon());

            this.persistedRelationshipType = relationshipType;

            var relationshipTypeGetParams:api_remote_relationshiptype.GetParams = {
                qualifiedName: relationshipType.getName(),
                format: 'XML'
            };

            new api_schema_relationshiptype.GetRelationshipTypeConfigByQualifiedNameRequest(relationshipType.getName()).send().done((response:any) => {
                this.relationshipTypeForm.setFormData({"xml": response.json.relationshipTypeXml});
            });

        }

        persistNewItem(successCallback?:() => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var createParams:api_remote_relationshiptype.CreateOrUpdateParams = {
                name: this.relationShipTypeWizardHeader.getName(),
                relationshipType: formData.xml,
                iconReference: this.getIconUrl()
            };

            api_remote_relationshiptype.RemoteRelationshipTypeService.relationshipType_createOrUpdate(createParams, () => {
                new app_wizard.RelationshipTypeCreatedEvent().fire();
                api_notify.showFeedback('Relationship type was created!');

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }

        updatePersistedItem(successCallback?:() => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var updateParams:api_remote_relationshiptype.CreateOrUpdateParams = {
                name: this.relationShipTypeWizardHeader.getName(),
                relationshipType: formData.xml,
                iconReference: this.getIconUrl()
            };

            api_remote_relationshiptype.RemoteRelationshipTypeService.relationshipType_createOrUpdate(updateParams, () => {
                new app_wizard.RelationshipTypeUpdatedEvent().fire();
                api_notify.showFeedback('Relationship type was saved!');

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }

    }
}