module api_app_wizard {

    export class WizardStep {
        private label:string;
        private panel:api_ui.Panel;
        private active:boolean;
        private el:api_dom.Element;
        private index:number;

        constructor(label:string, panel:api_ui.Panel) {
            this.label = label;
            this.panel = panel;
        }

        setIndex(index:number) {
            this.index = index;
        }

        getIndex() {
            return this.index;
        }

        setEl(el:api_dom.Element) {
            this.el = el;
        }

        setActive(active:boolean) {
            this.active = active;
            if (active) {
                this.el.getEl().addClass("active");
            } else {
                this.el.getEl().removeClass("active");
            }
        }

        isActive():boolean {
            return this.active;
        }

        getEl():api_dom.Element {
            return this.el;
        }

        getLabel():string {
            return this.label;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }
    }
}