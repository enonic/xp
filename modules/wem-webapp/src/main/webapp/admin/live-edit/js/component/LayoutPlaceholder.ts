module LiveEdit.component {
    export class LayoutPlaceholder extends ComponentPlaceholder {

        constructor() {
            this.setComponentType(new ComponentType(Type.LAYOUT));
            super();

            this.getEl().setData('live-edit-type', "layout");
        }

        onSelect() {
            super.onSelect();
        }

        onDeselect() {
            super.onDeselect();
        }
    }
}