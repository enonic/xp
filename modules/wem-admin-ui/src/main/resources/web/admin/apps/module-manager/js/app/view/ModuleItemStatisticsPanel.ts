module app.view {

    export class ModuleItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.module.ModuleSummary> {

        private upgradeNeeded:boolean = true;
        private upgradeMessageContainer:api.dom.DivEl;
        private moduleDataContainer:api.dom.DivEl;

        constructor() {
            super();
            this.addClass("module-item-statistics-panel");

            this.upgradeMessageContainer = new api.dom.DivEl("upgrade-message-container");
            if (this.upgradeNeeded) {
                this.upgradeMessageContainer.getEl().setInnerHtml("Upgrade Available - 1.0.3");
                this.appendChild(this.upgradeMessageContainer)
            }

            this.moduleDataContainer = new api.dom.DivEl("module-data-container");
            this.appendChild(this.moduleDataContainer);
        }

        setItem(item: api.app.view.ViewItem<api.module.ModuleSummary>) {
            super.setItem(item);
            this.moduleDataContainer.removeChildren();

            var infoGroup = new ModuleItemDataGroup("Info");
            infoGroup.addDataList("Build date", "TBA");
            infoGroup.addDataList("Version", item.getModel().getVersion());
            infoGroup.addDataList("ModuleID", item.getModel().getModuleKey().toString());
            infoGroup.addDataList("Requirements", "Enonic 5.0.0");

            var schemasGroup = new ModuleItemDataGroup("Schemas");
            schemasGroup.addDataList("Content Types", "Content Type1", "Content Type2", "Content Type3");
            schemasGroup.addDataList("Mixins", "TBA");

            this.moduleDataContainer.appendChild(infoGroup);
            this.moduleDataContainer.appendChild(schemasGroup);
        }

    }

    export class ModuleItemDataGroup extends api.dom.DivEl {

        private header:api.dom.H2El;

        constructor(title:string) {
            super("module-item-data-group");
            this.header = new api.dom.H2El();
            this.header.getEl().setInnerHtml(title);
            this.appendChild(this.header);
        }

        addDataList(header:string, ...datas:string[]) {
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
