module LiveEdit.component {
    export class TextPlaceholder extends ComponentPlaceholder {

        constructor() {
            this.setComponentType(new ComponentType(Type.TEXT));
            super();

            this.getEl().setData('live-edit-type', 'text');

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