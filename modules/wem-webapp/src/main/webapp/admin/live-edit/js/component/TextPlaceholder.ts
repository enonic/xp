module LiveEdit.component {
    export class TextPlaceholder extends ComponentPlaceholder {


        constructor() {
            this.setComponentType(new ComponentType(Type.PARAGRAPH));
            super();

            this.getEl().setData('live-edit-type', "paragraph");

        }

        onSelect() {
            super.onSelect();
        }

        onDeselect() {
            super.onDeselect();
        }
    }
}