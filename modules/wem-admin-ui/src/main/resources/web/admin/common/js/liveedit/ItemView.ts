module api.liveedit {

    import ComponentPath = api.content.page.ComponentPath;

    export class ItemView extends api.dom.Element {

        private type: ItemType;

        constructor(type: ItemType, element?: HTMLElement) {

            this.type = type;

            var props = new api.dom.ElementProperties();
            props.setGenerateId(false);
            if (element) {
                props.setHelper(new api.dom.ElementHelper(element));
            }
            else {
                props.setTagName("div");
            }
            super(props);

        }

        getType(): ItemType {
            return this.type;
        }

        isEmpty(): boolean {
            return this.getEl().hasAttribute('data-live-edit-empty-component');
        }

        isSelected(): boolean {
            return this.getEl().hasAttribute('data-live-edit-selected');
        }

        setComponentPath(path: ComponentPath) {
            this.getEl().setData('live-edit-component', path.toString());
        }

        hasComponentPath(): boolean {
            return this.getEl().hasAttribute('data-live-edit-component');
        }

        getComponentPath(): ComponentPath {
            var asString = this.getEl().getData('live-edit-component');
            return api.content.page.ComponentPath.fromString(asString);
        }

        getParentRegion(): RegionView {
            api.util.assert(this.type.isPageComponentType(),
                    "Expected to only be called when this is a PageComponent: " + api.util.getClassName(this));

            var parentElement = this.getHTMLElement().parentElement;
            if (!parentElement) {
                return null;
            }
            var type = ItemType.fromHTMLElement(parentElement);
            if (!type || !type.equals(RegionItemType.get())) {
                return null;
            }

            return RegionView.fromHTMLElement(parentElement);
        }

        getPrecedingComponentPath(): ComponentPath {
            api.util.assert(this.type.isPageComponentType(),
                    "Expected to only be called when this is a PageComponent: " + api.util.getClassName(this));

            var previousElement = this.getPreviousElement();
            if (!previousElement) {
                return null;
            }

            var asString = previousElement.getEl().getData('live-edit-component');
            return api.content.page.ComponentPath.fromString(asString);
        }
    }
}