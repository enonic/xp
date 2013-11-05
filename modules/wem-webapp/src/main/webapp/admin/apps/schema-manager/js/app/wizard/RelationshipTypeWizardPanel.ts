module app_wizard {

    export class RelationshipTypeWizardPanel extends api_app_wizard.WizardPanel<api_schema_relationshiptype.RelationshipType> {

        public static NEW_WIZARD_HEADER = "New Relationship Type";

        private static DEFAULT_SCHEMA_ICON_URL :string = api_util.getRestUri('schema/image/RelationshipType:_');

        private formIcon :api_app_wizard.FormIcon;

        private relationShipTypeWizardHeader :api_app_wizard.WizardHeaderWithName;

        private persistedRelationshipType :api_schema_relationshiptype.RelationshipType;

        private relationshipTypeForm :RelationshipTypeForm;

        constructor() {
            this.relationShipTypeWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon =
            new api_app_wizard.FormIcon(RelationshipTypeWizardPanel.DEFAULT_SCHEMA_ICON_URL, "Click to upload icon",
                api_util.getRestUri("upload"));

            var actions = new RelationshipTypeWizardActions(this);

            var toolbar = new RelationshipTypeWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            super({
                formIcon: this.formIcon,
                toolbar: toolbar,
                actions: actions,
                header: this.relationShipTypeWizardHeader
            });

            this.relationShipTypeWizardHeader.setName(RelationshipTypeWizardPanel.NEW_WIZARD_HEADER);
            this.relationshipTypeForm = new RelationshipTypeForm();
            this.addStep(new api_app_wizard.WizardStep("Relationship Type"), this.relationshipTypeForm);
        }

        setPersistedItem(relationshipType:api_schema_relationshiptype.RelationshipType){
            super.setPersistedItem(relationshipType);

            this.relationShipTypeWizardHeader.setName(relationshipType.getName());
            this.formIcon.setSrc(relationshipType.getIcon());

            this.persistedRelationshipType = relationshipType;

            new api_schema_relationshiptype.GetRelationshipTypeConfigByQualifiedNameRequest(relationshipType.getName()).send().
                done((response:api_rest.JsonResponse <api_schema_relationshiptype.GetRelationshipTypeConfigResult>) => {
                    this.relationshipTypeForm.setFormData({"xml": response.getResult().relationshipTypeXml});
            });
        }

        persistNewItem(successCallback ? : () => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var request = new api_schema_relationshiptype.CreateRelationshipTypeRequest(formData.xml, this.getIconUrl());
            request.send().done((response:api_rest.JsonResponse<any>)=> {
                var jsonResponse = response.getJson();
                if (jsonResponse.error) {
                    api_notify.showError(jsonResponse.error.msg);
                } else {
                    var relationshipType:api_schema_relationshiptype.RelationshipType = new api_schema_relationshiptype.RelationshipType(jsonResponse.result);
                    this.setPersistedItem(relationshipType);
                    new app_wizard.RelationshipTypeCreatedEvent().fire();
                    api_notify.showFeedback('Relationship type was created!');

                    new api_schema.SchemaCreatedEvent( api_schema.SchemaKind.RELATIONSHIP_TYPE, relationshipType.getName() ).fire();

                    if (successCallback) {
                        successCallback.call(this);
                    }
                }
            });
        }

        updatePersistedItem(successCallback ? : () => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var request = new api_schema_relationshiptype.UpdateRelationshipTypeRequest(formData.xml, this.getIconUrl());
            request.send().done((response:api_rest.JsonResponse<any>)=> {
                var jsonResponse = response.getJson();
                if (jsonResponse.error) {
                    api_notify.showError(jsonResponse.error.msg);
                } else {
                    var relationshipType:api_schema_relationshiptype.RelationshipType = new api_schema_relationshiptype.RelationshipType(jsonResponse.result);
                    new app_wizard.RelationshipTypeUpdatedEvent().fire();
                    api_notify.showFeedback('Relationship type was saved!');

                    new api_schema.SchemaUpdatedEvent( api_schema.SchemaKind.RELATIONSHIP_TYPE, relationshipType.getName() ).fire();

                    if (successCallback) {
                        successCallback.call(this);
                    }
                }
            });
        }
    }
}