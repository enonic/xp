module api.liveedit {

    import Content = api.content.Content;
    import PageComponent = api.content.page.PageComponent;

    export class PageItemViews {

        private itemViewCounter: number = 0;

        private pageView: PageView;

        private viewsById: {[s:number] : ItemView;} = {};

        constructor(pageView: PageView, views: ItemView[]) {

            this.pageView = pageView;
            views.forEach((view: ItemView) => {
                var newItemId = this.nextItemViewId();
                view.setItemId(newItemId);
                this.viewsById[newItemId.toNumber()] = view;

                // logging...
                var extra = "";
                if (api.ObjectHelper.iFrameSafeInstanceOf(view, PageComponentView)) {
                    var pageComponentView = <PageComponentView<PageComponent>>view;
                    if (pageComponentView.hasComponentPath()) {
                        extra = pageComponentView.getComponentPath().toString();
                    }
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(view, RegionView)) {
                    var regionView = <RegionView>view;
                    extra = regionView.getRegionPath().toString();
                }
                console.debug("PageItemViews: " + view.getItemId().toString() + " : " + view.getType().getShortName() + " : " + extra);
            });
        }

        private nextItemViewId(): ItemViewId {
            return new ItemViewId(++this.itemViewCounter)
        }

        addItemView(itemView: ItemView) {

            var existingItemViewId = itemView.getItemId();

            if (existingItemViewId) {

                console.debug("PageItemView.addItemView replaced view with id: " + existingItemViewId);
            }
            else {
                itemView.setItemId(this.nextItemViewId());
                console.debug("PageItemView.addItemView added view with id: " + itemView.getItemId());
            }
            this.viewsById[itemView.getItemId().toNumber()] = itemView;
        }

        removePageComponentView(pageComponentView: PageComponentView<PageComponent>) {
            this.removeItemViewById(pageComponentView.getItemId());
            new PageComponentRemoveEvent(pageComponentView).fire();
        }

        removeItemViewById(idToRemove: ItemViewId) {
            api.util.assertNotNull(idToRemove, "id cannot be null");
            console.debug("PageItemViews.removeItemViewById(" + idToRemove.toString() + ")");

            delete this.viewsById[idToRemove.toNumber()];
        }

        initializeEmpties() {

            for (var key in this.viewsById) {
                if (this.viewsById.hasOwnProperty(key)) {
                    var itemView = this.viewsById[key];
                    if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageComponentView)) {
                        var pageComponentView = <PageComponentView<PageComponent>>itemView;
                        if (pageComponentView.isEmpty()) {
                            pageComponentView.empty();
                        }
                    }
                    else if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, RegionView)) {
                        var regionView = <RegionView>itemView;
                        if (regionView.isRegionEmpty()) {
                            regionView.empty();
                        }
                    }
                }
            }
        }

        getByItemId(id: ItemViewId): ItemView {
            api.util.assertNotNull(id, "value cannot be null");
            return this.viewsById[id.toNumber()];
        }

        getItemViewByElement(element: HTMLElement): ItemView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            var itemView = this.getByItemId(itemId);
            api.util.assertNotNull(itemView, "ItemView not found: " + itemId.toString());

            console.debug("PageItemViews.getItemViewByElement itemId: " + itemId.toString() + ", type: " +
                          itemView.getType().getShortName());
            return  itemView;
        }

        getRegionViewByElement(element: HTMLElement): RegionView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }
            console.debug("PageItemViews.getRegionViewByElement itemId: " + itemId);

            var itemView = this.getByItemId(itemId);
            api.util.assertNotNull(itemView, "ItemView not found: " + itemId.toString());

            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, RegionView)) {
                return <RegionView>itemView;
            }
            return null;
        }

        getPageComponentViewByElement(element: HTMLElement): PageComponentView<PageComponent> {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }
            console.debug("PageItemViews.getPageComponentViewByElement itemId: " + itemId);

            var itemView = this.getByItemId(itemId);
            api.util.assertNotNull(itemView, "ItemView not found: " + itemId.toString());
            if (api.ObjectHelper.iFrameSafeInstanceOf(itemView, PageComponentView)) {
                return <PageComponentView<PageComponent>>itemView;
            }
            return null;
        }

        hasSelectedView(): boolean {
            return !!this.getSelectedView();
        }

        getSelectedView(): ItemView {
            for (var id in this.viewsById) {
                if (this.viewsById.hasOwnProperty(id) && this.viewsById[id].isSelected()) {
                    return this.viewsById[id];
                }
            }
            return null;
        }

        deselectSelectedView() {
            var selectedView = this.getSelectedView();
            if (selectedView) {
                this.getSelectedView().deselect();
            }
        }
    }
}