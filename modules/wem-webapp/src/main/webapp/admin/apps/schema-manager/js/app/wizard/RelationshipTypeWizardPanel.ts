module app.wizard {

    import RelationshipType = api.schema.relationshiptype.RelationshipType;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;
    import RelationshipTypeIconUrlResolver = api.schema.relationshiptype.RelationshipTypeIconUrlResolver;
    import RelationshipTypeResourceRequest = api.schema.relationshiptype.RelationshipTypeResourceRequest;
    import GetRelationshipTypeConfigByNameRequest = api.schema.relationshiptype.GetRelationshipTypeConfigByNameRequest;
    import CreateRelationshipTypeRequest = api.schema.relationshiptype.CreateRelationshipTypeRequest;
    import UpdateRelationshipTypeRequest = api.schema.relationshiptype.UpdateRelationshipTypeRequest;

    export class RelationshipTypeWizardPanel extends api.app.wizard.WizardPanel<api.schema.relationshiptype.RelationshipType> {

        public static NEW_WIZARD_HEADER = "New Relationship Type";

        private formIcon: api.app.wizard.FormIcon;

        private relationshipTypeIcon: api.icon.Icon;

        private relationShipTypeWizardHeader: api.app.wizard.WizardHeaderWithName;

        private relationshipTypeForm: RelationshipTypeForm;

        private persistedRelationshipType: RelationshipType;

        private persistedConfig: string;

        constructor(tabId: api.app.AppBarTabId, persistedRelationshipType: RelationshipType,
                    callback: (wizard: RelationshipTypeWizardPanel) => void) {
            this.relationShipTypeWizardHeader = new api.app.wizard.WizardHeaderWithName();
            this.formIcon = new api.app.wizard.FormIcon(new RelationshipTypeIconUrlResolver().resolveDefault(),
                "Click to upload icon",
                api.util.getRestUri("blob/upload"));

            this.formIcon.onUploadFinished((event: api.app.wizard.UploadFinishedEvent) => {
                this.relationshipTypeIcon = new api.icon.IconBuilder().
                    setBlobKey(event.getUploadItem().getBlobKey()).setMimeType(event.getUploadItem().getMimeType()).build();

                this.formIcon.setSrc(api.util.getRestUri('blob/' + this.relationshipTypeIcon.getBlobKey()));
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

        layoutPersistedItem(persistedRelationshipType: RelationshipType): Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.relationShipTypeWizardHeader.setName(persistedRelationshipType.getName());
            this.formIcon.setSrc(persistedRelationshipType.getIconUrl());
            this.persistedRelationshipType = persistedRelationshipType;

            return new GetRelationshipTypeConfigByNameRequest(persistedRelationshipType.getRelationshiptypeName()).send().
                then((response: api.rest.JsonResponse <api.schema.relationshiptype.GetRelationshipTypeConfigResult>):void => {

                    this.relationshipTypeForm.setFormData({"xml": response.getResult().relationshipTypeXml});
                    this.persistedConfig = response.getResult().relationshipTypeXml || "";
                });
        }

        persistNewItem(): Q.Promise<RelationshipType> {

            var deferred = Q.defer<RelationshipType>();

            var formData = this.relationshipTypeForm.getFormData();
            var newName = new RelationshipTypeName(this.relationShipTypeWizardHeader.getName());
            return new CreateRelationshipTypeRequest(newName, formData.xml, this.relationshipTypeIcon).
                sendAndParse().
                then((relationshipType: RelationshipType) => {

                    this.getTabId().changeToEditMode(relationshipType.getKey());
                    new app.wizard.RelationshipTypeCreatedEvent().fire();
                    api.notify.showFeedback('Relationship type was created!');

                    new api.schema.SchemaCreatedEvent(relationshipType).fire();

                    return relationshipType;
                });
        }

        updatePersistedItem(): Q.Promise<RelationshipType> {

            var deferred = Q.defer<RelationshipType>();

            var formData = this.relationshipTypeForm.getFormData();
            var newName = new RelationshipTypeName(this.relationShipTypeWizardHeader.getName());
            return new UpdateRelationshipTypeRequest(this.getPersistedItem().getRelationshiptypeName(),
                newName, formData.xml, this.relationshipTypeIcon).
                sendAndParse().
                then((relationshipType: RelationshipType)=> {

                    new app.wizard.RelationshipTypeUpdatedEvent().fire();
                    api.notify.showFeedback('Relationship type was saved!');

                    new api.schema.SchemaUpdatedEvent(relationshipType).fire();

                    return relationshipType;
                });
        }

        hasUnsavedChanges(): boolean {
            var persistedRelationshipType: RelationshipType = this.getPersistedItem();
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