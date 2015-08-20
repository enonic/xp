module app.wizard {

    import ItemView = api.liveedit.ItemView;
    import ItemType = api.liveedit.ItemType;
    import PageView = api.liveedit.PageView;
    import PageItemType = api.liveedit.PageItemType;
    import Content = api.content.Content;

    export class PageComponentsItemViewer extends api.ui.NamesAndIconViewer<ItemView> {

        private content: Content;

        constructor(content: Content) {
            this.content = content;
            super('page-components-item-viewer');
        }

        resolveDisplayName(object: ItemView): string {
            return object.getName();
        }


        resolveSubName(object: ItemView, relativePath: boolean = false): string {
            return object.getType() ? object.getType().getShortName() : "";
        }

        resolveIconUrl(object: ItemView): string {
            if (PageItemType.get().equals(object.getType())) {
                return new api.content.ContentIconUrlResolver().setContent(this.content).resolve();
            }
            return null;
        }

        resolveIconClass(object: ItemView): string {
            return api.liveedit.ItemViewIconClassResolver.resolve(object);
        }
    }

}