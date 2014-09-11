module api.liveedit {

    import PageComponent = api.content.page.PageComponent;
    import ComponentName = api.content.page.ComponentName;

    export class PageComponentViewContextMenuTitle<PAGE_COMPONENT extends PageComponent> extends ItemViewContextMenuTitle {

        constructor(pageComponent: PAGE_COMPONENT, type: PageComponentItemType) {
            pageComponent.onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == PageComponent.PROPERTY_NAME) {
                    this.setMainName((<ComponentName> event.getNewValue()).toString());
                }
            });
            super(pageComponent.getName().toString(), type.getConfig().getIconCls());
        }

    }

}