module api.liveedit {

    import Content = api.content.Content;
    import PageComponent = api.content.page.PageComponent;

    export class PageItemViews {

        private pageView: PageView;
        private views: ItemView[];

        constructor(pageView: PageView, views: ItemView[]) {

            this.pageView = pageView;
            views.forEach((view: ItemView, index: number) => {
                view.setItemId(index + 1);
            });
            this.views = views;
        }

        getPageView(): PageView {
            return this.pageView;
        }

        addItemView(view: ItemView) {
            this.views.push(view);
            console.log("PageItemView.addItemView view with id: " + this.views.length);
            view.setItemId(this.views.length);
        }

        removeItemViewById(id: number) {
            this.views.splice(id - 1);
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

        getByItemId(value: number): ItemView {
            return this.views[value - 1];
        }

        getItemViewByElement(element: HTMLElement): ItemView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
            console.log("PageItemViews.getItemViewByElement itemId: " + itemId);
            return this.getByItemId(itemId);
        }

        getRegionViewByElement(element: HTMLElement): RegionView {
            api.util.assertNotNull(element, "element cannot be null");

            var itemId = ItemView.parseItemId(element);
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
            console.log("PageItemViews.getPageComponentViewByElement itemId: " + itemId);
            var view = this.getByItemId(itemId);
            if (api.ObjectHelper.iFrameSafeInstanceOf(view, PageComponentView)) {
                return <PageComponentView<PageComponent>>view;
            }
            return null;
        }


    }
}