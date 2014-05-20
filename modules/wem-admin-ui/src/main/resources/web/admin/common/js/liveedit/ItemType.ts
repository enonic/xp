module api.liveedit {

    import PageComponentType = api.content.page.PageComponentType;

    export class ItemType {

        private static shortNameToInstance: {[shortName: string]: ItemType} = {};

        private shortName: string;

        constructor(shortName: string) {
            ItemType.shortNameToInstance[shortName] = this;
            this.shortName = shortName;
        }

        getShortName(): string {
            return this.shortName;
        }

        isPageComponentType(): boolean {
            return false
        }

        toPageComponentType(): PageComponentType {
            api.util.assert(this.isPageComponentType(), "Not support when ItemType is not a PageComponentType");
            return PageComponentType.byShortName(this.shortName);
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