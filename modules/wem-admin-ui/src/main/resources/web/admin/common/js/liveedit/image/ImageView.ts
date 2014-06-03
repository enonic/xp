module api.liveedit.image {

    import PageComponentView = api.liveedit.PageComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;

    export class ImageView extends PageComponentView {

        private placeholder : ImagePlaceholder;

        constructor(element?: HTMLElement, dummy?: boolean) {
            super(ImageItemType.get(), element, dummy);

            this.placeholder = new ImagePlaceholder(this);
        }

        showHighlighter(value: boolean) {

        }

        select() {
            super.select();
            if( this.isEmpty() ) {
                this.placeholder.select();
            }
        }

        deselect() {
            super.deselect();
            if( this.isEmpty() ) {
                this.placeholder.deselect();
            }
        }

        empty() {
            super.empty();

            this.removeChildren();
            this.appendChild(this.placeholder);

        }

        duplicate(): ImageView {

            var duplicatedView = new ImageView();
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }
    }
}