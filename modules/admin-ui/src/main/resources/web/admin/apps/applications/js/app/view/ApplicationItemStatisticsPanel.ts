module app.view {

    import ApplicationBrowseActions = app.browse.ApplicationBrowseActions;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import Mixin = api.schema.mixin.Mixin;
    import RelationshipType = api.schema.relationshiptype.RelationshipType;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PartDescriptor = api.content.page.region.PartDescriptor;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import ItemDataGroup = api.app.view.ItemDataGroup;

    export class ApplicationItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.application.Application> {

        private applicationDataContainer: api.dom.DivEl;
        private actionMenu: api.ui.menu.ActionMenu;

        constructor() {
            super("application-item-statistics-panel");

            this.actionMenu =
            new api.ui.menu.ActionMenu("Application actions", ApplicationBrowseActions.get().START_APPLICATION,
                ApplicationBrowseActions.get().STOP_APPLICATION);

            this.appendChild(this.actionMenu);

            this.applicationDataContainer = new api.dom.DivEl("application-data-container");
            this.appendChild(this.applicationDataContainer);
        }

        setItem(item: api.app.view.ViewItem<api.application.Application>) {
            var currentItem = this.getItem();

            if (currentItem && currentItem.equals(item)) {
                // do nothing in case item has not changed
                return;
            }

            super.setItem(item);
            var currentApplication = item.getModel();
            this.actionMenu.setLabel(api.util.StringHelper.capitalize(currentApplication.getState()));

            if (currentApplication.isStarted()) {
                ApplicationBrowseActions.get().START_APPLICATION.setEnabled(false);
                ApplicationBrowseActions.get().STOP_APPLICATION.setEnabled(true);
            } else {
                ApplicationBrowseActions.get().START_APPLICATION.setEnabled(true);
                ApplicationBrowseActions.get().STOP_APPLICATION.setEnabled(false);
            }

            this.applicationDataContainer.removeChildren();

            var infoGroup = new ItemDataGroup("Info", "info");
            infoGroup.addDataList("Build date", "TBA");
            infoGroup.addDataList("Version", currentApplication.getVersion());
            infoGroup.addDataList("Key", currentApplication.getApplicationKey().toString());
            infoGroup.addDataList("System Required",
                ">= " + currentApplication.getMinSystemVersion() + " and <=" + currentApplication.getMaxSystemVersion());

            var schemasGroup = new ItemDataGroup("Schemas", "schemas");

            var applicationKey = currentApplication.getApplicationKey();
            var schemaPromises = [
                new api.schema.content.GetContentTypesByApplicationRequest(applicationKey).sendAndParse(),
                new api.schema.mixin.GetMixinsByApplicationRequest(applicationKey).sendAndParse(),
                new api.schema.relationshiptype.GetRelationshipTypesByApplicationRequest(applicationKey).sendAndParse()
            ];

            wemQ.all(schemaPromises).
                spread((contentTypes: ContentTypeSummary[], mixins: Mixin[], relationshipTypes: RelationshipType[]) => {
                    var contentTypeNames = contentTypes.map((contentType: ContentTypeSummary) => contentType.getContentTypeName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                    schemasGroup.addDataArray("Content Types", contentTypeNames);

                    var mixinsNames = mixins.map((mixin: Mixin) => mixin.getMixinName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                    schemasGroup.addDataArray("Mixins", mixinsNames);

                    var relationshipTypeNames = relationshipTypes.map((relationshipType: RelationshipType) => relationshipType.getRelationshiptypeName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                    schemasGroup.addDataArray("RelationshipTypes", relationshipTypeNames);
                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();

            var descriptorsGroup = new ItemDataGroup("Descriptors", "descriptors");
            var descriptorPromises = [
                new api.content.page.GetPageDescriptorsByApplicationRequest(applicationKey).sendAndParse(),
                new api.content.page.region.GetPartDescriptorsByApplicationRequest(applicationKey).sendAndParse(),
                new api.content.page.region.GetLayoutDescriptorsByApplicationRequest(applicationKey).sendAndParse()
            ];

            wemQ.all(descriptorPromises).
                spread((pageDescriptors: PageDescriptor[], partDescriptors: PartDescriptor[], layoutDescriptors: LayoutDescriptor[]) => {
                    var pageNames = pageDescriptors.map((descriptor: PageDescriptor) => descriptor.getName().toString()).sort(this.sortAlphabeticallyAsc);
                    descriptorsGroup.addDataArray("Page", pageNames);

                    var partNames = partDescriptors.map((descriptor: PartDescriptor) => descriptor.getName().toString()).sort(this.sortAlphabeticallyAsc);
                    descriptorsGroup.addDataArray("Part", partNames);

                    var layoutNames = layoutDescriptors.map((descriptor: LayoutDescriptor) => descriptor.getName().toString()).sort(this.sortAlphabeticallyAsc);
                    descriptorsGroup.addDataArray("Layout", layoutNames);
                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();

            this.applicationDataContainer.appendChild(infoGroup);
            this.applicationDataContainer.appendChild(schemasGroup);
            this.applicationDataContainer.appendChild(descriptorsGroup);
        }

        private sortAlphabeticallyAsc(a: string, b: string): number {
            return a.toLocaleLowerCase().localeCompare(b.toLocaleLowerCase());
        }

    }

}
