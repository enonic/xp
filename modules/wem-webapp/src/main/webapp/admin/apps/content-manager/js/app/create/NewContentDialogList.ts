module app.create {

    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import SiteTemplateSummary = api.content.site.template.SiteTemplateSummary;

    export class NewContentDialogList extends api.ui.selector.list.ListBox<NewContentDialogListItem> {

        private selectedListeners: {(event: NewContentDialogItemSelectedEvent):void}[] = [];

        constructor() {
            super('content-types-list');
        }

        onSelected(listener: (event: NewContentDialogItemSelectedEvent)=>void) {
            this.selectedListeners.push(listener);
        }

        unSelected(listener: (event: NewContentDialogItemSelectedEvent)=>void) {
            this.selectedListeners = this.selectedListeners.filter((currentListener: (event: NewContentDialogItemSelectedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        private notifySelected(listItem: NewContentDialogListItem) {
            this.selectedListeners.forEach((listener: (event: NewContentDialogItemSelectedEvent)=>void) => {
                listener.call(this, new NewContentDialogItemSelectedEvent(listItem));
            });
        }

        createListItem(item: NewContentDialogListItem): api.dom.Element {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();
            namesAndIconView
                .setIconUrl(item.getIconUrl())
                .setMainName(item.getDisplayName())
                .setSubName(item.getName())
                .setDisplayIconLabel(item.isSiteTemplate());

            var itemEl = new api.dom.LiEl('content-types-list-item' + (item.isSiteTemplate() ? ' site' : ''));
            itemEl.appendChild(namesAndIconView);
            itemEl.onClicked((event: MouseEvent) => this.notifySelected(item));
            return itemEl;
        }
    }

}