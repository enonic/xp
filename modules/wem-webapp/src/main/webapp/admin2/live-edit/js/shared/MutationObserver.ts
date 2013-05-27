module LiveEdit {
    var $ = $liveedit;

    export class MutationObserver {

        private mutationSummary:any = null;
        private observedComponent:JQuery = null;

        constructor() {
            this.registerGlobalListeners();
        }


        private registerGlobalListeners():void {
            $(window).on('paragraphEdit.liveEdit.component', (event:JQueryEventObject, component:JQuery) => this.observe(event, component));
            $(window).on('click.liveEdit.shader', (event:JQueryEventObject) => this.disconnect(event));
        }


        private observe(event:JQueryEventObject, component:JQuery):void {
            var isBeingObserved:Boolean = this.observedComponent && this.observedComponent[0] === component[0];
            if (isBeingObserved) {
                return;
            }

            this.disconnect(event);
            this.observedComponent = component;
            this.mutationSummary = new LiveEditMutationSummary({
                callback: (summaries:any) => {
                    this.onMutate(summaries, event);
                },
                rootNode: component[0],
                queries: [
                    { all: true}
                ]
            });

            console.log('MutationObserver: start observing component', component);
        }


        private disconnect(event:JQueryEventObject):void {
            var targetComponentIsSelected = (this.observedComponent && this.observedComponent.hasClass('live-edit-selected-component'));
            var componentIsSelectedAndUserMouseOut = event.type === 'mouseOut.liveEdit.component' && targetComponentIsSelected;
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
                var component:JQuery = $(summaries[0].target),
                    targetComponentIsSelected = component.hasClass('live-edit-selected-component'),
                    componentIsNotSelectedAndMouseIsOver = !targetComponentIsSelected && event.type === 'mouseOver.liveEdit.component',
                    componentIsParagraphAndBeingEdited = component.attr('contenteditable');
                if (componentIsParagraphAndBeingEdited) {
                    $(window).trigger('paragraphEdit.liveEdit.component', [component]);
                } else if (componentIsNotSelectedAndMouseIsOver) {
                    $(window).trigger('mouseOver.liveEdit.component', [component]);
                } else {
                    $(window).trigger('select.liveEdit.component', [component]);
                }
            }
        }

    }
}