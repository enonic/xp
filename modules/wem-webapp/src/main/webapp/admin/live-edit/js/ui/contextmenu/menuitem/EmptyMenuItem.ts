module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class EmptyMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu: LiveEdit.ui.contextmenu.ContextMenu) {
            super({
                text: 'Empty',
                name: 'clear',
                handler: (event: Event) => {
                    this.onEmptyComponent();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onEmptyComponent() {
            var selectedComponent = this.menu.selectedComponent;
            var componentEl: JQuery = selectedComponent.getElement();
            var component = LiveEdit.component.Component.fromJQuery(componentEl);

            LiveEdit.component.Selection.deselect();

            $(window).trigger('componentReset.liveEdit', [selectedComponent]);

            var type = selectedComponent.getComponentType().getType();
            var emptyComponent = LiveEdit.component.ComponentPlaceholder.fromComponent(type);
            emptyComponent.setComponentPath(component.getComponentName());

            componentEl.replaceWith(emptyComponent.getHTMLElement());
            emptyComponent.init();
        }
    }
}