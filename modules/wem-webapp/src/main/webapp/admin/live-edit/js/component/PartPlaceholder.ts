module LiveEdit.component {
    export class PartPlaceholder extends ComponentPlaceholder {

        constructor() {
            this.setComponentType(new ComponentType(Type.PART));
            super();

            this.getEl().setData('live-edit-type', "part");
        }

        onSelect() {
            super.onSelect();
        }

        onDeselect() {
            super.onDeselect();
        }
    }
}