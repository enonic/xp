module api.ui {

    /**
     * An abstract class capable of viewing a given object.
     */
    export class Viewer<OBJECT> extends api.dom.DivEl {

        private object: OBJECT;

        setObject(object: OBJECT) {
            this.object = object;
        }

        getObject(): OBJECT {
            return this.object;
        }

        getPreferredHeight(): number {
            throw new Error("Must be implemented by inheritors");
        }
    }
}