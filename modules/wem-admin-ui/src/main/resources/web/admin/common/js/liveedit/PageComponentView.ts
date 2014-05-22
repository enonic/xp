module api.liveedit {

    import Content = api.content.Content;

    export class PageComponentView extends ItemView {

        constructor(type: ItemType, element?: HTMLElement, dummy?: boolean) {
            super(type, element, dummy);
        }

        select() {
            new PageComponentSelectEvent(this.getComponentPath(), this).fire();
            super.select();
        }
    }
}