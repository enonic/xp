module api.liveedit {

    import Component = api.content.page.region.Component;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import ComponentName = api.content.page.region.ComponentName;

    export class ComponentViewContextMenuTitle<COMPONENT extends Component> extends ItemViewContextMenuTitle {

        constructor(component: COMPONENT, type: ComponentItemType) {
            component.onPropertyChanged((event: ComponentPropertyChangedEvent) => {
                if (event.getPropertyName() == Component.PROPERTY_NAME) {
                    this.setMainName(component.getName().toString());
                }
            });
            super(component.getName().toString(), type.getConfig().getIconCls());
        }

    }

}