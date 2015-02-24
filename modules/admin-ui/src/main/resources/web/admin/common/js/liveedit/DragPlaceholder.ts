module api.liveedit {

    import Component = api.content.page.region.Component;

    export class DragPlaceholder extends ItemViewPlaceholder {

        private itemType: ItemType;

        private pattern: string = 'Drop {0} here';

        private regionView: RegionView;

        private messageEl: api.dom.DivEl;

        private static instance: DragPlaceholder;

        public static debug = true;

        public static get(): DragPlaceholder {
            if (!DragPlaceholder.instance) {
                if (DragPlaceholder.debug) {
                    console.log('DragPlaceholder.get() creating new instance');
                }
                DragPlaceholder.instance = new DragPlaceholder();
            }
            return DragPlaceholder.instance;
        }

        constructor() {
            super();
            this.setId('drag-placeholder').addClass('drag-placeholder');
            this.messageEl = new api.dom.DivEl("message");
            this.appendChild(this.messageEl);
        }

        setItemType(type: ItemType): DragPlaceholder {
            if (DragPlaceholder.debug) {
                console.log('DragPlaceholder.setItemType', type);
            }
            if (this.itemType) {
                this.removeClass(this.itemType.getShortName() + '-placeholder');
            }
            this.itemType = type;
            if (type) {
                this.setText(this.getDefaultText());
                this.addClass(type.getShortName() + '-placeholder');
            } else {
                this.setText('');
            }
            return this;
        }

        private getDefaultText() {
            return api.util.StringHelper.format(this.pattern, api.util.StringHelper.capitalize(this.itemType.getShortName()));
        }

        setDropAllowed(allowed: boolean): DragPlaceholder {
            if (DragPlaceholder.debug) {
                console.log('DragPlaceholder.seDropAllowed: ' + allowed);
            }
            if (allowed && this.itemType) {
                this.setText(this.getDefaultText());
            }
            this.toggleClass('drop-allowed', allowed);
            return this;
        }

        setText(text: string): DragPlaceholder {
            if (DragPlaceholder.debug) {
                console.log('DragPlaceholder.setText: ' + text);
            }
            this.messageEl.setHtml(text);
            return this;
        }

        setRegionView(regionView: RegionView): DragPlaceholder {
            if (DragPlaceholder.debug) {
                console.log('DragPlaceholder.setRegionView: ' + (regionView ? regionView.toString() : ''));
            }
            this.regionView = regionView;
            this.setDropAllowed(!!regionView);
            return this;
        }

        reset(): DragPlaceholder {
            this.setItemType(null);
            this.setRegionView(null);
            return this;
        }


    }
}