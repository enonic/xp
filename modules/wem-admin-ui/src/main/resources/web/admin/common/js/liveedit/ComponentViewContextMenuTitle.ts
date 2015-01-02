module api.liveedit {

    import Component = api.content.page.Component;
    import ComponentName = api.content.page.ComponentName;

    export class ComponentViewContextMenuTitle<COMPONENT extends Component> extends ItemViewContextMenuTitle {

        constructor(component: COMPONENT, type: ComponentItemType) {
            component.onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == Component.PROPERTY_NAME) {
                    this.setMainName((<ComponentName> event.getNewValue()).toString());
                }
            });
            super(component.getName().toString(), type.getConfig().getIconCls());
        }

    }

}