module api.liveedit {

    import Component = api.content.page.Component;
    import ComponentName = api.content.page.ComponentName;

    export class ComponentViewContextMenuTitle<PAGE_COMPONENT extends Component> extends ItemViewContextMenuTitle {

        constructor(pageComponent: PAGE_COMPONENT, type: PageComponentItemType) {
            pageComponent.onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == Component.PROPERTY_NAME) {
                    this.setMainName((<ComponentName> event.getNewValue()).toString());
                }
            });
            super(pageComponent.getName().toString(), type.getConfig().getIconCls());
        }

    }

}