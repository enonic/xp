module api.liveedit {

    import Component = api.content.page.region.Component;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import ComponentName = api.content.page.region.ComponentName;

    export class ComponentViewContextMenuTitle<COMPONENT extends Component> extends ItemViewContextMenuTitle {

        constructor(component: COMPONENT, type: ComponentItemType) {
            var handler = (event: ComponentPropertyChangedEvent) => {
                if (event.getPropertyName() == Component.PROPERTY_NAME) {
                    this.setMainName(createMainName(component));
                }
            },
                createMainName = (component: COMPONENT): string => {
                    return component.getName() ? component.getName().toString() : "";
                };
            
            super(createMainName(component), type.getConfig().getIconCls());

            component.onPropertyChanged(handler);
            this.onRemoved(() => component.unPropertyChanged(handler));
        }


    }

}