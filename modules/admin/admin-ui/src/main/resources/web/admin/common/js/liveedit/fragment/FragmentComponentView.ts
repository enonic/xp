module api.liveedit.fragment {

    import ComponentView = api.liveedit.ComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import FragmentComponent = api.content.page.region.FragmentComponent;

    export class FragmentComponentViewBuilder extends ComponentViewBuilder<FragmentComponent> {

        constructor() {
            super();
            this.setType(FragmentItemType.get());
        }
    }

    export class FragmentComponentView extends ComponentView<FragmentComponent> {

        private fragment: api.dom.Element;
        private fragmentComponent: FragmentComponent;

        constructor(builder: FragmentComponentViewBuilder) {
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.fragmentComponent = builder.component;

            super(builder.setPlaceholder(
                new FragmentPlaceholder(this)).setTooltipViewer(new FragmentComponentViewer()).setInspectActionRequired(true));

            this.initializeFragment();
        }

        private initializeFragment() {

            //var figureElChildren = this.getChildren();
            //for (var i = 0; i < figureElChildren.length; i++) {
            //    var image = figureElChildren[i];
            //    if (image.getHTMLElement().tagName.toUpperCase() == 'IMG') {
            //        this.fragment = image;
            //
            //        // no way to use ImgEl.onLoaded because all html tags are parsed as Element
            //        this.fragment.getEl().addEventListener("load", (event) => {
            //            // refresh shader and highlighter after image loaded
            //            // if it's still selected
            //            if (this.isSelected()) {
            //                this.highlight();
            //                this.shade();
            //            }
            //        });
            //    }
            //    return;
            //}
        }

        isEmpty(): boolean {
            return !this.fragmentComponent || this.fragmentComponent.isEmpty();
        }

    }
}