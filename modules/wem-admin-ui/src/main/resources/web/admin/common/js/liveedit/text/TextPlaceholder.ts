module api.liveedit.text {

    export class TextPlaceholder extends api.dom.DivEl {

        constructor() {
            super('text-placeholder');
            this.getEl().setInnerHtml('Click to edit');

            this.onClicked((event: MouseEvent) => {
                console.log('edit');

                event.stopPropagation();
                event.preventDefault();
                return false;
            });
        }
    }
}