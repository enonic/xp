module app.wizard.page {

    import RegionPath = api.content.page.RegionPath;
    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentType = api.content.page.PageComponentType;
    import ItemView = api.liveedit.ItemView;

    export class PageComponentDuplicatedEvent {

        private itemView: ItemView;

        private type: PageComponentType;

        private region: RegionPath;

        private path: ComponentPath;

        constructor(itemView: ItemView, type: PageComponentType, region: RegionPath, path: ComponentPath) {
            this.itemView = itemView;
            this.type = type;
            this.region = region;
            this.path = path;
        }

        getItemView(): ItemView {
            return this.itemView;
        }

        getType(): PageComponentType {
            return this.type;
        }

        getRegion(): RegionPath {
            return this.region;
        }

        getPath(): ComponentPath {
            return this.path;
        }
    }
}