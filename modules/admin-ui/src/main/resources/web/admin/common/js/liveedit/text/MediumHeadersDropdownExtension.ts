module api.liveedit.text {

    export class MediumHeadersDropdownExtension implements MediumEditorExtension {

        private button: api.dom.ButtonEl;

        private icon: api.dom.IEl;

        private editor: MediumEditorType;

        private dropdownButtons: api.dom.UlEl;

        constructor() {
            this.button = new api.dom.ButtonEl('medium-editor-action');
            this.button.onClicked(this.onClick.bind(this));
            this.icon = new api.dom.IEl('icon-headers');
            this.button.appendChild(this.icon);
        }

        onClick(event: MouseEvent): void {

        }

        getButton(): HTMLElement {
            return this.button.getHTMLElement();
        }


        beforeCheckState(): void {
            this.icon.removeClass('icon-headers');
            this.icon.removeClass('icon-header1');
            this.icon.removeClass('icon-header2');
            this.icon.removeClass('icon-header3');
            this.icon.removeClass('icon-header4');
            this.button.removeClass('medium-editor-button-active');
        }

        checkState(node: Element): void {
            if (this.icon.getClass() !== '') {
                return;
            }

            var tagName = node.tagName.toLowerCase();
            if (tagName === 'h1') {
                this.icon.setClass('icon-header1');
                this.button.addClass('medium-editor-button-active');
            } else if (tagName === 'h2') {
                this.icon.setClass('icon-header2');
                this.button.addClass('medium-editor-button-active');
            } else if (tagName === 'h3') {
                this.icon.setClass('icon-header3');
                this.button.addClass('medium-editor-button-active');
            } else if (tagName === 'h4') {
                this.icon.setClass('icon-header4');
                this.button.addClass('medium-editor-button-active');
            }
        }

        afterCheckState(): void {
            if (this.icon.getClass() === '') {
                this.icon.setClass('icon-headers');
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
            var header4Item = this.createHeaderMenuItem('h4', 'icon-header4', true);

            this.dropdownButtons.appendChildren(header1Item, header2Item, header3Item, header4Item);
            this.button.appendChild(this.dropdownButtons);

            mainButtonParent.appendChild(this.dropdownButtons.getHTMLElement());
        }

        private createHeaderMenuItem(elementTag: string, iconClass: string, lastButton: boolean = false): api.dom.LiEl {
            var menuItem = new api.dom.LiEl();
            var button = new api.dom.ButtonEl('medium-editor-action');
            if (lastButton) {
                button.addClass('medium-editor-button-bottom');
            }
            var icon = new api.dom.IEl(iconClass);
            button.appendChild(icon);
            button.onClicked((event) => {
                event.preventDefault();
                event.stopPropagation();
                // apply header action to selected text
                this.editor.execAction('append-' + elementTag, event);
                this.icon.setClass(iconClass);
                this.button.addClass('medium-editor-button-active');
            });

            menuItem.appendChild(button);
            return menuItem;
        }
    }
}