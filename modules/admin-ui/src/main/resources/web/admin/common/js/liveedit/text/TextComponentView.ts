module api.liveedit.text {

    import ComponentView = api.liveedit.ComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.region.TextComponent;


    export class TextComponentViewBuilder extends ComponentViewBuilder<TextComponent> {
        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextComponentView extends ComponentView<TextComponent> implements api.ui.text.TextEditorEditableArea {

        private textComponent: TextComponent;

        private article: api.dom.Element;

        constructor(builder: TextComponentViewBuilder) {

            this.liveEditModel = builder.parentRegionView.liveEditModel;
            this.textComponent = builder.component;

            super(builder.
                setContextMenuActions(this.createTextContextMenuActions()).
                setPlaceholder(new TextPlaceholder(this)).
                setTooltipViewer(new TextComponentViewer()));

            this.addClass('text-view');


            if (!this.isEmpty()) {
                // article comes from server
                this.article = this.getChildren()[0];
            } else {
                // create it in case of new component
                this.article = new api.dom.ArticleEl();
                this.appendChild(this.article);
            }

            this.onKeyDown(this.handleKey.bind(this));
            this.onKeyUp(this.handleKey.bind(this));
            this.onDblClicked(this.handleDbClick.bind(this));
        }

        getElement(): api.dom.Element {
            return this;
        }

        processChanges() {
            this.textComponent.setText(this.article.getHtml());
            new TextComponentEditedEvent(this).fire();
        }

        isEmpty(): boolean {
            return !this.textComponent || this.textComponent.isEmpty();
        }

        duplicate(duplicate: TextComponent): TextComponentView {
            var duplicatedView = new TextComponentView(new TextComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setComponent(duplicate));

            duplicatedView.insertAfterEl(this);
            return duplicatedView;
        }

        handleDbClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            this.setEditMode(true, true);
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            if (!this.isSelected()) {
                this.deselectParent();
                this.select(!this.isEmpty() ? {x: event.pageX, y: event.pageY} : null);
            } else {
                this.setEditMode(true, false);
            }
        }

        handleKey() {
            this.processChanges();
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);
        }

        deselect(silent?: boolean) {
            super.deselect(silent);
            this.setEditMode(false);
        }


        private setEditMode(flag: boolean, selectText?: boolean) {

            this.toggleClass('edit', flag);
            this.article.getEl().setAttribute('contenteditable', flag.toString());

            if (flag) {
                this.hideTooltip();
                this.hideContextMenu();

                this.article.giveFocus();
                this.focusText(selectText);

                api.ui.text.TextEditorToolbar.get().showToolbar(this);
                new TextComponentStartEditingEvent(this).fire();
            } else {
                api.ui.text.TextEditorToolbar.get().hideToolbar();
            }
        }

        private focusText(selectText: boolean) {
            var element = this.getHTMLElement();
            if (selectText) {
                var selection = window.getSelection();
                var range = document.createRange();
                range.selectNodeContents(element);
                range.setStart(range.endContainer, range.startOffset);
                range.setEnd(range.endContainer, range.endOffset);
                selection.removeAllRanges();
                selection.addRange(range);
            } else {
                element.focus();
                element.click();
            }
        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                this.setEditMode(true, false);
            }));
            return actions;
        }
    }
}