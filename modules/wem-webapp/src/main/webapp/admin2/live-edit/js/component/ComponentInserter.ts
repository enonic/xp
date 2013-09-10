module LiveEdit.component {
    export class ComponentInserter {

        // Uses
        static $ = $liveEdit;

        public static replaceEmptyComponent(componentKey:string):void {
            var selectedComponent = LiveEdit.Selection.getSelectedComponent();

            if (!selectedComponent.isEmpty()) {
                return;
            }

            var urlToMockComponent:string = '../../../admin2/live-edit/data/mock-component-' + componentKey + '.html';

            $.ajax({
                url: urlToMockComponent,
                cache: false
            }).done((responseHtml:string) => {

                var responseHtmlAsElement:JQuery = $(responseHtml);

                selectedComponent.getElement().replaceWith(responseHtmlAsElement);

                LiveEdit.Selection.setSelectionOnElement(responseHtmlAsElement);

                $(window).trigger('selectComponent.liveEdit', [new LiveEdit.component.Component(responseHtmlAsElement)]);

                $(window).trigger('sortableUpdate.liveEdit');

                // It seems like it is not possible to add new sortables (region in layout) to the existing sortable
                // So we have to create it again.
                // Ideally we should destroy the existing sortable first before creating.
                if (selectedComponent.getComponentType().getType() == LiveEdit.component.Type.LAYOUT) {
                    LiveEdit.component.DragDropSort.createJQueryUiSortable();
                }

            });

        }
    }
}
