module app.wizard {

    export class RelationshipTypeWizardPanel extends api.app.wizard.WizardPanel<api.schema.relationshiptype.RelationshipType> {

        public static NEW_WIZARD_HEADER = "new relationship type";

        private formIcon: api.app.wizard.FormIcon;

        private relationshipTypeIcon: api.icon.Icon;

        private relationShipTypeWizardHeader: api.app.wizard.WizardHeaderWithName;

        private relationshipTypeForm: RelationshipTypeForm;

        private persistedRelationshipType: api.schema.relationshiptype.RelationshipType;

        private persistedConfig: string;

        constructor(tabId: api.app.AppBarTabId, persistedRelationshipType: api.schema.relationshiptype.RelationshipType,
                    callback: (wizard: RelationshipTypeWizardPanel) => void) {
            this.relationShipTypeWizardHeader = new api.app.wizard.WizardHeaderWithName();
            this.formIcon = new api.app.wizard.FormIcon(new api.schema.relationshiptype.RelationshipTypeIconUrlResolver().resolveDefault(),
                "Click to upload icon",
                api.util.getRestUri("blob/upload"));

            this.formIcon.addListener({
                onUploadStarted: null,
                onUploadFinished: (uploadItem: api.ui.UploadItem) => {
                    this.relationshipTypeIcon = new api.icon.IconBuilder().
                        setBlobKey(uploadItem.getBlobKey()).setMimeType(uploadItem.getMimeType()).build();

                    this.formIcon.setSrc(api.util.getRestUri('blob/' + this.relationshipTypeIcon.getBlobKey()));
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

            var steps: api.app.wizard.WizardStep[] = [];
            steps.push(new api.app.wizard.WizardStep("Relationship Type", this.relationshipTypeForm));

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

        setPersistedItem(relationshipType: api.schema.relationshiptype.RelationshipType, callback: Function) {
            super.setPersistedItem(relationshipType, () => {
                this.relationShipTypeWizardHeader.setName(relationshipType.getName());
                this.formIcon.setSrc(relationshipType.getIconUrl());
                this.persistedRelationshipType = relationshipType;

                new api.schema.relationshiptype.GetRelationshipTypeConfigByNameRequest(relationshipType.getRelationshiptypeName()).send().
                    done((response: api.rest.JsonResponse <api.schema.relationshiptype.GetRelationshipTypeConfigResult>) => {
                        this.relationshipTypeForm.setFormData({"xml": response.getResult().relationshipTypeXml});
                        this.persistedConfig = response.getResult().relationshipTypeXml || "";
                        callback();
                    });
            });
        }

        persistNewItem(callback: (persistedRelationshipType: api.schema.relationshiptype.RelationshipType) => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var newName = new api.schema.relationshiptype.RelationshipTypeName(this.relationShipTypeWizardHeader.getName());
            var request = new api.schema.relationshiptype.CreateRelationshipTypeRequest(newName, formData.xml, this.relationshipTypeIcon);
            request.sendAndParse().
                done((relationshipType: api.schema.relationshiptype.RelationshipType)=> {

                    this.setPersistedItem(relationshipType, () => {

                        this.getTabId().changeToEditMode(relationshipType.getKey());
                        new app.wizard.RelationshipTypeCreatedEvent().fire();
                        api.notify.showFeedback('Relationship type was created!');

                        new api.schema.SchemaCreatedEvent(relationshipType).fire();

                        callback(relationshipType);
                    });
                });
        }

        updatePersistedItem(callback: (persistedRelationshipType: api.schema.relationshiptype.RelationshipType) => void) {
            var formData = this.relationshipTypeForm.getFormData();
            var newName = new api.schema.relationshiptype.RelationshipTypeName(this.relationShipTypeWizardHeader.getName());
            var request = new api.schema.relationshiptype.UpdateRelationshipTypeRequest(this.getPersistedItem().getRelationshiptypeName(),
                newName, formData.xml, this.relationshipTypeIcon);
            request.sendAndParse().
                done((relationshipType: api.schema.relationshiptype.RelationshipType)=> {

                    this.setPersistedItem(relationshipType, () => {

                        new app.wizard.RelationshipTypeUpdatedEvent().fire();
                        api.notify.showFeedback('Relationship type was saved!');

                        new api.schema.SchemaUpdatedEvent(relationshipType).fire();

                        callback(relationshipType);
                    });
                });
        }

        hasUnsavedChanges(): boolean {
            var persistedRelationshipType: api.schema.relationshiptype.RelationshipType = this.getPersistedItem();
            if (persistedRelationshipType == undefined) {
                return true;
            } else {
                return !api.util.isStringsEqual(persistedRelationshipType.getName(), this.relationShipTypeWizardHeader.getName())
                    || !api.util.isStringsEqual(api.util.removeCarriageChars(this.persistedConfig),
                                                api.util.removeCarriageChars(this.relationshipTypeForm.getFormData().xml));
            }
        }
    }
}