module LiveEdit.component {
    export class ComponentPlaceholder extends api.dom.DivEl {
        constructor() {
            super();
            this.addClass("live-edit-empty-component");
            this.getEl().setData('live-edit-empty-component', "true");

            console.log("attaching onSelectEvent");
//            $(window).on('componentSelect.liveEdit', (event, name?)=> {
//                console.log(event.currentTarget, this.getHTMLElement());
//                if (event.currentTarget == this.getHTMLElement()) {
//                    this.onSelect();
//                }
//            });
            LiveEdit.event.ComponentSelectedEvent.on(() => {
                this.onSelect();
            });

            LiveEdit.event.ComponentDeselectedEvent.on(() => {
                this.onDeselect();
            });
        }

        getRegionName():string {
            var regionName = $(this.getHTMLElement()).parent('[data-live-edit-region]').attr('data-live-edit-region');
            console.log("FOUND REGION NAME", regionName);
            return regionName;
        }

        getComponentName():string {
            return this.getEl().getData('live-edit-component');
        }

        getComponentPath(): string {
            return this.getEl().getData('live-edit-component');
        }

        onSelect() {
            console.log("selected component!", this);
        }

        onDeselect() {
            console.log("deselected component!", this);
        }

        isSelected():boolean {
            return this.getEl().getAttribute("data-live-edit-selected") == "true";
        }
    }
}