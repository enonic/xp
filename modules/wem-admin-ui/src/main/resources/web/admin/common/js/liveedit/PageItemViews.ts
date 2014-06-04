module api.liveedit {

    import Content = api.content.Content;
    import PageComponent = api.content.page.PageComponent;

    export class PageItemViews {

        private pageView: PageView;
        private views: ItemView[];

        constructor(pageView: PageView, views: ItemView[]) {

            this.pageView = pageView;
            views.forEach((view: ItemView, index: number) => {
                view.setItemId(new ItemViewId(index + 1));
            });
            this.views = views;
        }

        getPageView(): PageView {
            return this.pageView;
        }

        addItemView(view: ItemView) {
            this.views.push(view);
            console.log("PageItemView.addItemView view with id: " + this.views.length);
            view.setItemId(new ItemViewId(this.views.length));
        }

        removeItemViewById(id: ItemViewId) {
            api.util.assertNotNull(id, "id cannot be null");
            this.views.splice(id.toNumber() - 1);
        }

        initializeEmpties() {
            this.views.forEach((view: ItemView) => {
                if (api.ObjectHelper.iFrameSafeInstanceOf(view, PageComponentView)) {
                    var pageComponentView = <PageComponentView<PageComponent>>view;
                    if (pageComponentView.isEmpty()) {
                        pageComponentView.empty();
                    }
                }
            });
        }

        getByItemId(id: ItemViewId): ItemView {
            api.util.assertNotNull(id, "value cannot be null");
            return this.views[id.toNumber() - 1];
        }

        getItemViewByElement(element: HTMLElement): ItemView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            console.log("PageItemViews.getItemViewByElement itemId: " + itemId);
            return this.getByItemId(itemId);
        }

        getRegionViewByElement(element: HTMLElement): RegionView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            console.log("PageItemViews.getRegionViewByElement itemId: " + itemId);
            var view = this.getByItemId(itemId);
            if (api.ObjectHelper.iFrameSafeInstanceOf(view, RegionView)) {
                return <RegionView>view;
            }
            return null;
        }

        getPageComponentViewByElement(element: HTMLElement): PageComponentView<PageComponent> {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            if (!itemId) {
                return null;
            }

            console.log("PageItemViews.getPageComponentViewByElement itemId: " + itemId);
            var view = this.getByItemId(itemId);
            if (api.ObjectHelper.iFrameSafeInstanceOf(view, PageComponentView)) {
                return <PageComponentView<PageComponent>>view;
            }
            return null;
        }
    }
}