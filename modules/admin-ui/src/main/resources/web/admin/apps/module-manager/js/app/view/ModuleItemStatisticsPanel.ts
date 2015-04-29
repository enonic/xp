module app.view {

    import ModuleBrowseActions = app.browse.ModuleBrowseActions;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import Mixin = api.schema.mixin.Mixin;
    import RelationshipType = api.schema.relationshiptype.RelationshipType;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PartDescriptor = api.content.page.region.PartDescriptor;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import ItemDataGroup = api.app.view.ItemDataGroup;

    export class ModuleItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.module.Module> {

        private moduleDataContainer: api.dom.DivEl;
        private actionMenu: api.ui.menu.ActionMenu;

        constructor() {
            super("module-item-statistics-panel");

            this.actionMenu =
            new api.ui.menu.ActionMenu("Module actions", ModuleBrowseActions.get().START_MODULE, ModuleBrowseActions.get().STOP_MODULE,
                ModuleBrowseActions.get().UNINSTALL_MODULE);

            this.appendChild(this.actionMenu);

            this.moduleDataContainer = new api.dom.DivEl("module-data-container");
            this.appendChild(this.moduleDataContainer);
        }

        setItem(item: api.app.view.ViewItem<api.module.Module>) {
            var currentItem = this.getItem();

            if (currentItem && currentItem.equals(item)) {
                // do nothing in case item has not changed
                return;
            }

            super.setItem(item);
            var currentModule = item.getModel();
            this.actionMenu.setLabel(api.util.StringHelper.capitalize(currentModule.getState()));

            if (currentModule.isStarted()) {
                ModuleBrowseActions.get().START_MODULE.setEnabled(false);
                ModuleBrowseActions.get().STOP_MODULE.setEnabled(true);
            } else {
                ModuleBrowseActions.get().START_MODULE.setEnabled(true);
                ModuleBrowseActions.get().STOP_MODULE.setEnabled(false);
            }

            this.moduleDataContainer.removeChildren();

            var infoGroup = new ItemDataGroup("Info", "info");
            infoGroup.addDataList("Build date", "TBA");
            infoGroup.addDataList("Version", currentModule.getVersion());
            infoGroup.addDataList("Key", currentModule.getModuleKey().toString());
            infoGroup.addDataList("System Required",
                ">= " + currentModule.getMinSystemVersion() + " and <=" + currentModule.getMaxSystemVersion());

            var schemasGroup = new ItemDataGroup("Schemas", "schemas");

            var moduleKey = currentModule.getModuleKey();
            var schemaPromises = [
                new api.schema.content.GetContentTypesByModuleRequest(moduleKey).sendAndParse(),
                new api.schema.mixin.GetMixinsByModuleRequest(moduleKey).sendAndParse(),
                new api.schema.relationshiptype.GetRelationshipTypesByModuleRequest(moduleKey).sendAndParse()
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
                new api.content.page.GetPageDescriptorsByModuleRequest(moduleKey).sendAndParse(),
                new api.content.page.region.GetPartDescriptorsByModuleRequest(moduleKey).sendAndParse(),
                new api.content.page.region.GetLayoutDescriptorsByModuleRequest(moduleKey).sendAndParse()
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

            this.moduleDataContainer.appendChild(infoGroup);
            this.moduleDataContainer.appendChild(schemasGroup);
            this.moduleDataContainer.appendChild(descriptorsGroup);
        }

        private sortAlphabeticallyAsc(a: string, b: string): number {
            return a.toLocaleLowerCase().localeCompare(b.toLocaleLowerCase());
        }

    }

}
