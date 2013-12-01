module app_wizard {

    export class RelationshipTypeWizardPanel extends api_app_wizard.WizardPanel<api_schema_relationshiptype.RelationshipType> {

        public static NEW_WIZARD_HEADER = "New Relationship Type";

        private static DEFAULT_SCHEMA_ICON_URL :string = api_util.getRestUri('schema/image/RelationshipType:_');

        private formIcon :api_app_wizard.FormIcon;

        private relationShipTypeWizardHeader :api_app_wizard.WizardHeaderWithName;

        private relationshipTypeForm :RelationshipTypeForm;

        constructor(tabId:api_app.AppBarTabId) {
            this.relationShipTypeWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon =
            new api_app_wizard.FormIcon(RelationshipTypeWizardPanel.DEFAULT_SCHEMA_ICON_URL, "Click to upload icon",
                api_util.getRestUri("upload"));

            var actions = new RelationshipTypeWizardActions(this);

            var mainToolbar = new RelationshipTypeWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            this.relationShipTypeWizardHeader.setName(RelationshipTypeWizardPanel.NEW_WIZARD_HEADER);
            this.relationshipTypeForm = new RelationshipTypeForm();

            var steps:api_app_wizard.WizardStep[] = [];
            steps.push(new api_app_wizard.WizardStep("Relationship Type", this.relationshipTypeForm));

            super({
                tabId: tabId,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.relationShipTypeWizardHeader,
                steps: steps
            });
        }

        setPersistedItem(relationshipType:api_schema_relationshiptype.RelationshipType){
            super.setPersistedItem(relationshipType);

            this.relationShipTypeWizardHeader.setName(relationshipType.getName());
            this.formIcon.setSrc(relationshipType.getIcon());

            new api_schema_relationshiptype.GetRelationshipTypeConfigByNameRequest(relationshipType.getRelationshiptypeName()).send().
                done((response:api_rest.JsonResponse <api_schema_relationshiptype.GetRelationshipTypeConfigResult>) => {
                    this.relationshipTypeForm.setFormData({"xml": response.getResult().relationshipTypeXml});
            });
        }

        persistNewItem(successCallback ? : () => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var newName = new api_schema_relationshiptype.RelationshipTypeName( this.relationShipTypeWizardHeader.getName() );
            var request = new api_schema_relationshiptype.CreateRelationshipTypeRequest(newName, formData.xml, this.getIconUrl());
            request.send().done((response:api_rest.JsonResponse<any>)=> {
                var jsonResponse = response.getJson();
                if (jsonResponse.error) {
                    api_notify.showError(jsonResponse.error.msg);
                } else {
                    var relationshipType:api_schema_relationshiptype.RelationshipType = new api_schema_relationshiptype.RelationshipType(jsonResponse.result);
                    this.setPersistedItem(relationshipType);
                    this.getTabId().changeToEditMode(relationshipType.getKey());
                    new app_wizard.RelationshipTypeCreatedEvent().fire();
                    api_notify.showFeedback('Relationship type was created!');

                    new api_schema.SchemaCreatedEvent( relationshipType ).fire();

                    if (successCallback) {
                        successCallback.call(this);
                    }
                }
            });
        }

        updatePersistedItem(successCallback ? : () => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var newName = new api_schema_relationshiptype.RelationshipTypeName( this.relationShipTypeWizardHeader.getName() );
            var request = new api_schema_relationshiptype.UpdateRelationshipTypeRequest(this.getPersistedItem().getRelationshiptypeName(),
                                                                                        newName, formData.xml, this.getIconUrl());
            request.send().done((response:api_rest.JsonResponse<any>)=> {
                var jsonResponse = response.getJson();
                if (jsonResponse.error) {
                    api_notify.showError(jsonResponse.error.msg);
                } else {
                    var relationshipType:api_schema_relationshiptype.RelationshipType = new api_schema_relationshiptype.RelationshipType(jsonResponse.result);
                    this.setPersistedItem(relationshipType);
                    new app_wizard.RelationshipTypeUpdatedEvent().fire();
                    api_notify.showFeedback('Relationship type was saved!');

                    new api_schema.SchemaUpdatedEvent( relationshipType ).fire();

                    if (successCallback) {
                        successCallback.call(this);
                    }
                }
            });
        }
    }
}