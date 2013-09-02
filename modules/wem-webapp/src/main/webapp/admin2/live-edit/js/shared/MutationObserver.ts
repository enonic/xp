module LiveEdit {

    // Uses
    var $ = $liveEdit;

    export class MutationObserver {

        private mutationSummary:any = null;
        private observedComponent:LiveEdit.component.Component = null;

        constructor() {
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('editParagraphComponent.liveEdit', (event:JQueryEventObject, component) => this.observe(event, component));
            $(window).on('clickShader.liveEdit', (event:JQueryEventObject) => this.disconnect(event));
        }

        private observe(event:JQueryEventObject, component:LiveEdit.component.Component):void {
            var isBeingObserved:Boolean = this.observedComponent && this.observedComponent.getElement()[0] === component.getElement()[0];
            if (isBeingObserved) {
                return;
            }

            this.disconnect(event);
            this.observedComponent = component;
            this.mutationSummary = new LiveEditMutationSummary({
                callback: (summaries:any) => {
                    this.onMutate(summaries, event);
                },
                rootNode: component.getElement()[0],
                queries: [
                    { all: true}
                ]
            });

            console.log('MutationObserver: start observing component', component);
        }

        private disconnect(event:JQueryEventObject):void {
            var targetComponentIsSelected = (this.observedComponent && this.observedComponent.isSelected());
            var componentIsSelectedAndUserMouseOut = event.type == 'mouseOutComponent.liveEdit' && targetComponentIsSelected;
            if (componentIsSelectedAndUserMouseOut) {
                return;
            }

            this.observedComponent = null;
            if (this.mutationSummary) {
                this.mutationSummary.disconnect();
                this.mutationSummary = null;

                console.log('MutationObserver: disconnected');
            }
        }

        private onMutate(summaries:any, event:JQueryEventObject):void {
            if (summaries && summaries[0]) {
                var component:LiveEdit.component.Component = new LiveEdit.component.Component($(summaries[0].target)),
                    targetComponentIsSelected = component.isSelected(),
                    componentIsNotSelectedAndMouseIsOver = !targetComponentIsSelected && event.type === 'mouseOverComponent.liveEdit',
                    componentIsParagraphAndBeingEdited = component.getComponentType().getType() === LiveEdit.component.Type.PARAGRAPH && component.getElement().attr('contenteditable');

                if (componentIsParagraphAndBeingEdited) {
                    $(window).trigger('editParagraphComponent.liveEdit', [ component ]);
                } else if (componentIsNotSelectedAndMouseIsOver) {
                    $(window).trigger('mouseOverComponent.liveEdit', [ component ]);
                } else {
                    $(window).trigger('selectComponent.liveEdit', [ component ]);
                }
            }
        }

    }
}