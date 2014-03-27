module LiveEdit.component {
    export class TextPlaceholder extends ComponentPlaceholder {

        constructor() {
            this.setComponentType(new ComponentType(Type.PARAGRAPH));
            super();

            this.getEl().setData('live-edit-type', 'paragraph');

            console.log('TextPlaceholder onResized');
            this.onResized((event: api.dom.ElementResizedEvent) => {
                console.log('resize', event);
            });

        }

        onSelect() {
            super.onSelect();
        }

        onDeselect() {
            super.onDeselect();
        }
    }
}