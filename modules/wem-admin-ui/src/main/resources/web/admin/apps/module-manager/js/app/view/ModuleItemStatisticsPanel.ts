module app.view {

    import ModuleBrowseActions = app.browse.ModuleBrowseActions;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import Mixin = api.schema.mixin.Mixin;
    import MetadataSchemaName = api.schema.metadata.MetadataSchemaName;
    import RelationshipType = api.schema.relationshiptype.RelationshipType;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;

    export class ModuleItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.module.Module> {

        private upgradeNeeded: boolean = true;
        private upgradeMessageContainer: api.dom.DivEl;
        private moduleDataContainer: api.dom.DivEl;
        private moduleActions: api.ui.Action[] = [];
        private actionMenu: api.ui.menu.ActionMenu;
        private currentItem: api.app.view.ViewItem<api.module.Module>;

        constructor() {
            super("module-item-statistics-panel");

            this.upgradeMessageContainer = new api.dom.DivEl("upgrade-message-container");
            if (this.upgradeNeeded) {
                this.upgradeMessageContainer.getEl().setInnerHtml("Upgrade Available - 1.0.3");
                this.appendChild(this.upgradeMessageContainer)
            }

            this.actionMenu =
            new api.ui.menu.ActionMenu("Module actions", ModuleBrowseActions.get().START_MODULE, ModuleBrowseActions.get().STOP_MODULE,
                ModuleBrowseActions.get().UNINSTALL_MODULE);

            this.appendChild(this.actionMenu);

            this.moduleDataContainer = new api.dom.DivEl("module-data-container");
            this.appendChild(this.moduleDataContainer);
        }

        setItem(item: api.app.view.ViewItem<api.module.Module>) {
            if (this.currentItem && this.currentItem.equals(item)) {
                // do nothing in case item has not changed
                return;
            }
            this.currentItem = item;

            super.setItem(item);
            var currentModule = item.getModel();
            this.actionMenu.setLabel(api.util.StringHelper.capitalize(currentModule.getState()));

            if (currentModule.getState() == "started") {
                ModuleBrowseActions.get().START_MODULE.setEnabled(false);
                ModuleBrowseActions.get().STOP_MODULE.setEnabled(true);
            } else {
                ModuleBrowseActions.get().START_MODULE.setEnabled(true);
                ModuleBrowseActions.get().STOP_MODULE.setEnabled(false);
            }

            this.moduleDataContainer.removeChildren();

            var infoGroup = new ModuleItemDataGroup("Info");
            infoGroup.addDataList("Build date", "TBA");
            infoGroup.addDataList("Version", currentModule.getVersion());
            infoGroup.addDataList("Key", currentModule.getModuleKey().toString());
            infoGroup.addDataList("System Required",
                    ">= " + currentModule.getMinSystemVersion() + " and <=" + currentModule.getMaxSystemVersion());

            var schemasGroup = new ModuleItemDataGroup("Schemas");

            var moduleKey = currentModule.getModuleKey();
            var promises = [
                new api.schema.content.GetContentTypesByModuleRequest(moduleKey).sendAndParse(),
                new api.schema.mixin.GetMixinsByModuleRequest(moduleKey).sendAndParse(),
                new api.schema.relationshiptype.GetRelationshipTypesByModuleRequest(moduleKey).sendAndParse()
            ];
            
            wemQ.all(promises).
                spread((contentTypes: ContentTypeSummary[], mixins: Mixin[], relationshipTypes: RelationshipType[]) => {
                    var contentTypeNames = contentTypes.map((contentType: ContentTypeSummary) => contentType.getContentTypeName().getLocalName());
                    schemasGroup.addDataArray("Content Types", contentTypeNames);

                    var mixinsNames = mixins.map((mixin: Mixin) => mixin.getMixinName().getLocalName());
                    schemasGroup.addDataArray("Mixins", mixinsNames);

                    var metadataSchemaNames = currentModule.getMetadataSchemaDependencies().map((name: MetadataSchemaName) => name.getLocalName());
                    schemasGroup.addDataArray("MetadataSchemas", metadataSchemaNames);

                    var relationshipTypeNames = relationshipTypes.map((relationshipType: RelationshipType) => relationshipType.getRelationshiptypeName().getLocalName());
                    schemasGroup.addDataArray("RelationshipTypes", relationshipTypeNames);
                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();

            this.moduleDataContainer.appendChild(infoGroup);
            this.moduleDataContainer.appendChild(schemasGroup);
        }

    }

    export class ModuleItemDataGroup extends api.dom.DivEl {

        private header: api.dom.H2El;

        constructor(title: string) {
            super("module-item-data-group");
            this.header = new api.dom.H2El();
            this.header.getEl().setInnerHtml(title);
            this.appendChild(this.header);
        }

        addDataList(header: string, ...datas: string[]) {
            this.addDataArray(header, datas);
        }

        addDataArray(header: string, datas: string[]) {
            var dataList = new api.dom.UlEl("data-list");

            if (header) {
                var headerElement = new api.dom.LiEl();
                headerElement.addClass("list-header");

                headerElement.getEl().setInnerHtml(header);
                dataList.appendChild(headerElement);
            }

            datas.forEach((data) => {
                var dataElement = new api.dom.LiEl();
                dataElement.getEl().setInnerHtml(data);
                dataList.appendChild(dataElement);
            });

            this.appendChild(dataList);
        }
    }

}
