module API_ui_toolbar {

    export class Toolbar extends API_ui.Component {

        ext;

        private components:any[] = [];

        private element:HTMLElement;

        constructor() {
            super("toolbar");
            this.element = this.createHTMLElement();
            this.initExt();
        }

        private createHTMLElement():HTMLElement {
            var divEl:HTMLElement = document.createElement("div");
            divEl.id = super.getId();
            divEl.className = 'toolbar';
            return divEl;
        }

        private initExt() {
            this.ext = new Ext.Component({
                contentEl: this.element,
                region: 'north'
            });
        }

        getHTMLElement():HTMLElement {
            return this.element;
        }

        addAction(action:API_action.Action) {
            var button:Button = this.doAddAction(action);
            this.element.appendChild(button.getHTMLElement())
        }

        addGreedySpacer() {
            var spacer = new ToolbarGreedySpacer();
            this.components.push( spacer );
        }

        private doAddAction(action:API_action.Action):Button {
            var button:API_ui_toolbar.Button = new API_ui_toolbar.Button(action);
            if( this.hasGreedySpacer() ) {
                button.setFloatRight(true);
            }
            this.components.push(button);
            return button;
        }

        private hasGreedySpacer():bool {
            for( var i in this.components ) {
                var comp = this.components[i];
                if( comp instanceof ToolbarGreedySpacer ) {
                    return true;
                }
            }
            return false;
        }
    }

    export class ToolbarGreedySpacer {

    }
}
