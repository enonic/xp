module app.create {

    import ValueChangedEvent = api.ui.ValueChangedEvent;

    export class NewContentDialogFacets extends api.dom.DivEl {

        public static ALL = 'all';
        public static CONTENT = 'content';
        public static SITES = 'sites';

        private contentFacet: api.dom.SpanEl;
        private sitesFacet: api.dom.SpanEl;
        private allFacet: api.dom.SpanEl;

        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        constructor() {
            super('content-type-facet');

            this.allFacet = new api.dom.SpanEl('all-facet');
            this.contentFacet = new api.dom.SpanEl('content-facet');
            this.sitesFacet = new api.dom.SpanEl('sites-facet');

            this.appendChild(this.allFacet);
            this.appendChild(this.contentFacet);
            this.appendChild(this.sitesFacet);

            this.allFacet.onClicked((event: MouseEvent) => this.setActiveFacet(NewContentDialogFacets.ALL));
            this.contentFacet.onClicked((event: MouseEvent) => this.setActiveFacet(NewContentDialogFacets.CONTENT));
            this.sitesFacet.onClicked((event: MouseEvent) => this.setActiveFacet(NewContentDialogFacets.SITES));

            this.updateLabels(0, 0);
            this.setActiveFacet(NewContentDialogFacets.ALL);
        }

        setActiveFacet(facet: string) {
            var oldValue = this.getActiveFacet();

            if (oldValue != facet) {
                this.getChildren().forEach((child:api.dom.Element) => child.removeClass('active'));

                if (facet == NewContentDialogFacets.ALL) {
                    this.allFacet.addClass('active');
                } else if (facet == NewContentDialogFacets.CONTENT) {
                    this.contentFacet.addClass('active');
                } else if (facet == NewContentDialogFacets.SITES) {
                    this.sitesFacet.addClass('active');
                }

                var newValue = this.getActiveFacet();
                this.notifyValueChanged(oldValue, newValue);
            }
        }

        getActiveFacet(): string {
            if (this.allFacet.hasClass('active')) {
                return NewContentDialogFacets.ALL;
            } else if (this.contentFacet.hasClass('active')) {
                return NewContentDialogFacets.CONTENT;
            } else if (this.sitesFacet.hasClass('active')) {
                return NewContentDialogFacets.SITES;
            } else {
                return '';
            }
        }

        updateLabels(contentTypesCount: number, siteTemplatesCount: number) {
            this.contentFacet.setHtml("Content (" + contentTypesCount + ")");
            this.sitesFacet.setHtml("Sites (" + siteTemplatesCount + ")");
            this.allFacet.setHtml("All (" + (siteTemplatesCount + contentTypesCount) + ")");
        }

        onValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((currentListener: (event: ValueChangedEvent) => void) => {
                return listener != currentListener;
            });
        }

        private notifyValueChanged(oldValue: string, newValue: string) {
            this.valueChangedListeners.forEach((listener: (event: ValueChangedEvent) => void) => {
                listener(new ValueChangedEvent(oldValue, newValue));
            });
        }

    }

}