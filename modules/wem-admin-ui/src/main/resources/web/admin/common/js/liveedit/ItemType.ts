module api.liveedit {

    export class ItemType {

        private static shortNameToInstance: {[shortName: string]: ItemType} = {};

        private shortName: string;

        private config: ItemTypeConfig;

        constructor(shortName: string, config: ItemTypeConfigJson) {
            ItemType.shortNameToInstance[shortName] = this;
            this.shortName = shortName;
            this.config = new ItemTypeConfig(config);
        }

        getShortName(): string {
            return this.shortName;
        }

        getConfig(): ItemTypeConfig {
            return this.config;
        }


        isPageComponentType(): boolean {
            return false
        }

        toPageComponentType(): api.content.page.PageComponentType {
            api.util.assert(this.isPageComponentType(), "Not support when ItemType is not a PageComponentType");
            return api.content.page.PageComponentType.byShortName(this.shortName);
        }

        createView(parent: any, data: any, element?: HTMLElement, dummy?: boolean): ItemView {
            throw new Error("Must be implemented by inheritors");
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ItemType)) {
                return false;
            }

            var other = <ItemType>o;

            if (!api.ObjectHelper.stringEquals(this.shortName, other.shortName)) {
                return false;
            }

            return true;
        }

        static getDraggables(): ItemType[] {
            var draggables: ItemType[] = [];
            for (var shortName in  ItemType.shortNameToInstance) {
                var itemType = ItemType.shortNameToInstance[shortName];
                if (itemType.getConfig().isDraggable()) {
                    draggables.push(itemType);
                }
            }
            return draggables;
        }

        static byShortName(shortName: string): ItemType {
            var itemType = ItemType.shortNameToInstance[shortName];
            api.util.assertNotNull(itemType, "Unknown ItemType: " + shortName);
            return  itemType;
        }

        static fromJQuery(element: JQuery): ItemType {
            return ItemType.fromHTMLElement(element.get(0));
        }

        static fromHTMLElement(element: HTMLElement): ItemType {
            var typeAsString = element.getAttribute("data-live-edit-type");
            return ItemType.byShortName(typeAsString);
        }

        static fromElement(element: api.dom.Element): ItemType {
            var typeAsString = element.getEl().getAttribute("data-live-edit-type");
            return ItemType.byShortName(typeAsString);
        }
    }
}