module LiveEdit.ui {

    // Uses
    var $ = $liveEdit;

    export class Cursor extends LiveEdit.ui.Base {

        bodyElement:JQuery;

        defaultBodyCursor:string;

        constructor() {
            super();

            this.bodyElement = $('body');

            // Cache any user set body@style cursor in order to restore it later.
            // Not 100% as the cursor can change any time during the page's life cycle.
            // $.css('cursor') should be avoided here used as it uses window.getComputedStyle()
            this.defaultBodyCursor = this.bodyElement[0].style.cursor;

            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('mouseOverComponent.liveEdit', (event:JQueryEventObject, component) => this.update(component));
            $(window).on('selectComponent.liveEdit', (event:JQueryEventObject, component) => this.update(component));
            $(window).on('mouseOutComponent.liveEdit', () => this.reset());
            $(window).on('sortableStart.liveEdit', () => this.hide());
            $(window).on('sortableStop.liveEdit', () => this.reset());
        }

        private update(component:LiveEdit.component.Component):void {
            this.bodyElement.css('cursor', component.getComponentType().getCursor());
        }

        private hide():void {
            this.bodyElement.css('cursor', 'none');
        }

        private reset():void {
            if(LiveEdit.component.DragDropSort.isDragging()) {
                return;
            }
            this.bodyElement.css('cursor', this.defaultBodyCursor || '');
        }

    }
}