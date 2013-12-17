module app_wizard {

    export class RelationshipTypeWizardPanel extends api_app_wizard.WizardPanel<api_schema_relationshiptype.RelationshipType> {

        public static NEW_WIZARD_HEADER = "New Relationship Type";

        private formIcon: api_app_wizard.FormIcon;

        private relationshipTypeIcon: api_icon.Icon;

        private relationShipTypeWizardHeader: api_app_wizard.WizardHeaderWithName;

        private relationshipTypeForm: RelationshipTypeForm;

        constructor(tabId: api_app.AppBarTabId, persistedRelationshipType: api_schema_relationshiptype.RelationshipType, callback: (wizard:RelationshipTypeWizardPanel) => void) {
            this.relationShipTypeWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon = new api_app_wizard.FormIcon(new api_schema_relationshiptype.RelationshipTypeIconUrlResolver().resolveDefault(),
                "Click to upload icon",
                api_util.getRestUri("blob/upload"));

            this.formIcon.addListener({
                onUploadStarted: null,
                onUploadFinished: (uploadItem: api_ui.UploadItem) => {
                    this.relationshipTypeIcon = new api_icon.IconBuilder().
                        setBlobKey(uploadItem.getBlobKey()).setMimeType(uploadItem.getMimeType()).build();

                    this.formIcon.setSrc(api_util.getRestUri('blob/' + this.relationshipTypeIcon.getBlobKey()));
                }
            });

            var actions = new RelationshipTypeWizardActions(this);

            var mainToolbar = new RelationshipTypeWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            this.relationShipTypeWizardHeader.setName(RelationshipTypeWizardPanel.NEW_WIZARD_HEADER);
            this.relationshipTypeForm = new RelationshipTypeForm();

            var steps: api_app_wizard.WizardStep[] = [];
            steps.push(new api_app_wizard.WizardStep("Relationship Type", this.relationshipTypeForm));

            super({
                tabId: tabId,
                persistedItem: persistedRelationshipType,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.relationShipTypeWizardHeader,
                steps: steps
            }, () => {
                callback(this);
            });
        }

        setPersistedItem(relationshipType: api_schema_relationshiptype.RelationshipType, callback:Function) {
            super.setPersistedItem(relationshipType, () => {
                this.relationShipTypeWizardHeader.setName(relationshipType.getName());
                this.formIcon.setSrc(relationshipType.getIconUrl());

                new api_schema_relationshiptype.GetRelationshipTypeConfigByNameRequest(relationshipType.getRelationshiptypeName()).send().
                    done((response: api_rest.JsonResponse <api_schema_relationshiptype.GetRelationshipTypeConfigResult>) => {
                        this.relationshipTypeForm.setFormData({"xml": response.getResult().relationshipTypeXml});
                        callback();
                    });
            });
        }

        persistNewItem(successCallback ?: () => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var newName = new api_schema_relationshiptype.RelationshipTypeName(this.relationShipTypeWizardHeader.getName());
            var request = new api_schema_relationshiptype.CreateRelationshipTypeRequest(newName, formData.xml, this.relationshipTypeIcon);
            request.sendAndParse().
                done((relationshipType: api_schema_relationshiptype.RelationshipType)=> {

                    this.setPersistedItem(relationshipType, () => {

                        this.getTabId().changeToEditMode(relationshipType.getKey());
                        new app_wizard.RelationshipTypeCreatedEvent().fire();
                        api_notify.showFeedback('Relationship type was created!');

                        new api_schema.SchemaCreatedEvent(relationshipType).fire();

                        if (successCallback) {
                            successCallback.call(this);
                        }
                    });
                });
        }

        updatePersistedItem(successCallback ?: () => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var newName = new api_schema_relationshiptype.RelationshipTypeName(this.relationShipTypeWizardHeader.getName());
            var request = new api_schema_relationshiptype.UpdateRelationshipTypeRequest(this.getPersistedItem().getRelationshiptypeName(),
                newName, formData.xml, this.relationshipTypeIcon);
            request.sendAndParse().
                done((relationshipType: api_schema_relationshiptype.RelationshipType)=> {

                    this.setPersistedItem(relationshipType, () => {

                        new app_wizard.RelationshipTypeUpdatedEvent().fire();
                        api_notify.showFeedback('Relationship type was saved!');

                        new api_schema.SchemaUpdatedEvent(relationshipType).fire();

                        if (successCallback) {
                            successCallback.call(this);
                        }
                    });
                });
        }
    }
}