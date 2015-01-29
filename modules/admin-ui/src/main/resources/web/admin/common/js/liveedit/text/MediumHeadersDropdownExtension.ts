module api.liveedit.text {

    export class MediumHeadersDropdownExtension implements MediumEditorExtension {

        private button: api.dom.ButtonEl;

        private editor: MediumEditorType;

        private dropdownButtons: api.dom.UlEl;

        constructor() {
            this.button = new api.dom.ButtonEl('medium-editor-action');
            this.button.onClicked(this.onClick.bind(this));
            var icon = new api.dom.IEl('icon-headers');
            this.button.appendChild(icon);
        }

        onClick(event: MouseEvent): void {

        }

        getButton(): HTMLElement {
            return this.button.getHTMLElement();
        }

        checkState(node: Element): void {
            if (node.tagName == 'h4') {
                this.button.addClass('medium-editor-button-active');
            }
        }

        setEditor(editor: MediumEditorType) {
            this.editor = editor;
        }

        onHideToolbar() {

        }

        onShowToolbar() {
            this.initDropdownButtons();
        }

        private initDropdownButtons() {
            var mainButtonParent = this.button.getHTMLElement().parentElement;
            if (!mainButtonParent) {
                return; // parent not set yet
            }
            if (this.dropdownButtons && mainButtonParent.contains(this.dropdownButtons.getHTMLElement())) {
                return; // dropdown buttons already added
            }

            // add dropdown buttons
            this.dropdownButtons = new api.dom.UlEl('medium-editor-toolbar-actions');
            this.dropdownButtons.addClass('clearfix');
            var header1Item = this.createHeaderMenuItem('h1', 'icon-header1');
            var header2Item = this.createHeaderMenuItem('h2', 'icon-header2');
            var header3Item = this.createHeaderMenuItem('h3', 'icon-header3');
            var header4Item = this.createHeaderMenuItem('h4', 'icon-header4');

            this.dropdownButtons.appendChildren(header1Item, header2Item, header3Item, header4Item);
            this.button.appendChild(this.dropdownButtons);

            mainButtonParent.appendChild(this.dropdownButtons.getHTMLElement());
        }

        private createHeaderMenuItem(elementTag: string, iconClass: string): api.dom.LiEl {
            var menuItem = new api.dom.LiEl();
            var button = new api.dom.ButtonEl('medium-editor-action');
            var icon = new api.dom.IEl(iconClass);
            button.appendChild(icon);
            button.onClicked((event) => {
                event.preventDefault();
                event.stopPropagation();
                // apply header action to selected text
                this.editor.execAction('append-' + elementTag, event);
            });

            menuItem.appendChild(button);
            return menuItem;
        }
    }
}