module api.liveedit.text {

    import LayoutItemType = api.liveedit.layout.LayoutItemType;


    export class TextPlaceholder extends api.dom.DivEl {

        private textView: TextView;

        private clickToEditLink: api.dom.AEl;

        constructor(textView: TextView) {
            super();
            this.textView = textView;
            this.clickToEditLink = null;

            this.onResized((event: api.dom.ElementResizedEvent) => {
                console.log('TextPlaceholder resize', event);
            });

        }

        private addClickToEdit() {
            this.clickToEditLink = new api.dom.AEl('text-link-click-to-edit');
            this.clickToEditLink.setText('Click to Edit');
            this.clickToEditLink.onClicked((event: MouseEvent) => {
                event.stopPropagation();
                event.preventDefault();

                this.removeChild(this.clickToEditLink);
                this.clickToEditLink = null;

                wemjq(this.getHTMLElement()).trigger('click');

                return false;
            });
            this.appendChild(this.clickToEditLink);
        }

        select() {
            if (this.textView.isEmpty() && !this.clickToEditLink) {
                this.addClickToEdit();
            }
        }

        deselect() {
            if (this.clickToEditLink) {
                this.removeChild(this.clickToEditLink);
                this.clickToEditLink = null;
            }
        }
    }
}