module api.ui {

    /**
     * An abstract class capable of viewing a given object.
     */
    export class Viewer<OBJECT> extends api.dom.Element {

        private object: OBJECT;

        constructor(className?: string) {
            super(new api.dom.NewElementBuilder().
                setTagName("div").
                setClassName('viewer ' + (className || '')).
                setGenerateId(false));
        }

        doRender(): Q.Promise<boolean> {
            return super.doRender().then((rendered) => {
                this.doLayout(this.getObject());
                return rendered;
            });
        }

        /*
         Need a sync method (instead of async doRender) to use in grid formatters which use viewer.toString()
         */
        protected doLayout(object: OBJECT) {
            // may be implemented in children
        }

        setObject(object: OBJECT) {
            this.object = object;

            if(this.isRendered()) {
                return this.doLayout(object);
            }
        }

        getObject(): OBJECT {
            return this.object;
        }

        getPreferredHeight(): number {
            throw new Error("Must be implemented by inheritors");
        }

        toString(): string {
            if(!this.isRendered()) {
                this.doLayout(this.getObject());
            }
            return super.toString();
        }
    }
}