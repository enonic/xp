module LiveEdit.component {
    // Uses
    var $ = $liveEdit;

    export class ComponentResizeObserver {

        private component:LiveEdit.component.Component;

        constructor() {
            this.registerGlobalListeners();
        }

        private observe(component:LiveEdit.component.Component):void {
            this.disconnect();

            if (component.isEmpty()) {
                return;
            }

            console.log('ComponentResizeListener.observe()');

            this.component = component;
            this.component.getElement().on('resize', () => {
                if (this.component.isSelected()) {
                    $(window).on('selectComponent.liveEdit', [component])
                }
            });
        }

        private disconnect():void {
            if (this.component != null) {
                console.log('ComponentResizeListener.disconnect()');

                this.component.getElement().off('resize');
            }
            this.component = null;
        }

        private registerGlobalListeners():void {
            $(window).on('selectComponent.liveEdit', (event:JQueryEventObject, component:LiveEdit.component.Component, pagePosition) => this.observe(component));
            $(window).on('deselectComponent.liveEdit', () => this.disconnect());
            $(window).on('editParagraphComponent.liveEdit', (event:JQueryEventObject, component:LiveEdit.component.Component) => this.observe(component));
        }

    }
}
