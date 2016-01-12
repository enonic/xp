module api.liveedit {

    import Component = api.content.page.region.Component;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import ComponentName = api.content.page.region.ComponentName;

    export class ComponentViewContextMenuTitle<COMPONENT extends Component> extends ItemViewContextMenuTitle {

        constructor(component: COMPONENT, type: ComponentItemType) {
            var handler = (event: ComponentPropertyChangedEvent) => {
                if (event.getPropertyName() == Component.PROPERTY_NAME) {
                    this.setMainName(this.createMainName(component, type));
                }
            };
            super(this.createMainName(component, type), type.getConfig().getIconCls());

            component.onPropertyChanged(handler);
            this.onRemoved(() => component.unPropertyChanged(handler));
        }

        private createMainName(component: COMPONENT, type: ComponentItemType): string {
            return component.getName() ? component.getName().toString() : type.toComponentType().getDefaultName();
        }

    }

}