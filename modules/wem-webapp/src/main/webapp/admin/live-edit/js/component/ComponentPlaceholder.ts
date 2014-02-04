module LiveEdit.component {
    export class ComponentPlaceholder extends api.dom.DivEl {
        constructor() {
            super();
            this.addClass("live-edit-empty-component");
            this.getEl().setData('live-edit-empty-component', "true");

            $liveEdit(this.getHTMLElement()).on('componentSelect.liveEdit', (event, name?)=> {
                this.onSelect();
            });

            $liveEdit(window).on('componentDeselect.liveEdit', (event, name?)=> {
                this.onDeselect();
            });
        }

        getPrecedingComponentPath():string {
            var previousComponent = api.dom.Element.fromHtmlElement($liveEdit(this.getHTMLElement()).prevAll('[data-live-edit-component]')[0])
            return previousComponent.getEl().getData("live-edit-component");
        }

        getRegionName():string {
            var regionName = $(this.getHTMLElement()).parent('[data-live-edit-region]').attr('data-live-edit-region');
            return regionName;
        }

        getComponentName():string {
            return this.getEl().getData('live-edit-component');
        }

        getComponentPath():string {
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