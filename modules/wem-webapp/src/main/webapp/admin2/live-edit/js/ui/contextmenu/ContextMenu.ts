module LiveEdit.ui.contextmenu {

    // Uses
    var $ = $liveEdit;

    export class ContextMenu extends LiveEdit.ui.Base {
        private selectedComponent:LiveEdit.component.Component;

        private hidden = true;

        private buttons = [];

        constructor() {
            super();
            this.addView();
            this.registerEvents();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('selectComponent.liveEdit', (event:JQueryEventObject, component:LiveEdit.component.Component, pagePosition) => this.show(component, pagePosition));
            $(window).on('deselectComponent.liveEdit', () => this.hide());
            $(window).on('componentRemoved.liveEdit', () => this.hide());
            $(window).on('editParagraphComponent.liveEdit', () => this.hide());
            $(window).on('sortableStart.liveEdit', () => this.fadeOutAndHide());
            $(window).on('resizeBrowserWindow.liveEdit', () => this.handleWindowResize());
        }

        private addView():void {
            var html:string = '';
            html += '<div class="live-edit-context-menu live-edit-arrow-top" style="display: none">';
            html += '   <div class="live-edit-context-menu-title-bar">';
            html += '       <div class="live-edit-context-menu-title-icon-container"><div><!-- --></div></div>';
            html += '       <div class="live-edit-context-menu-title-text"><!-- populated --></div>';
            html += '       <div class="live-edit-context-menu-title-close-button"><!-- --></div>';
            html += '   </div>';
            html += '   <div class="live-edit-context-menu-items"><!-- populated --></div>';
            html += '</div>';

            this.createHtmlFromString(html);
            this.appendTo($('body'));
            this.addButtons();
        }

        private registerEvents():void {
            this.getEl().draggable({
                handle: '.live-edit-context-menu-title-bar',
                addClasses: false
            });

            this.getCloseButton().click(function () {
                $(window).trigger('deselectComponent.liveEdit');
            });
        }

        private show(component:LiveEdit.component.Component, pagePosition):void {
            this.selectedComponent = component;

            this.updateTitleBar(component);
            this.updateMenuItemsForComponent(component);

            // Calculate positions after menu is populated in order to get the right position.
            var pageXPosition = pagePosition.x - this.getEl().width() / 2,
                pageYPosition = pagePosition.y + 15;

            this.moveToXY(pageXPosition, pageYPosition);
            this.getEl().show(null);

            this.hidden = false;
        }

        private hide():void {
            this.selectedComponent = null;
            this.getEl().hide(null);
            this.hidden = true;
        }

        private fadeOutAndHide():void {
            this.getEl().fadeOut(500, () => {
                this.hide();
                $(window).trigger('deselectComponent.liveEdit', {showComponentBar: false});
            });
            this.selectedComponent = null;
        }

        private moveToXY(x, y):void {
            this.getEl().css({
                left: x,
                top: y
            });
        }

        private addButtons():void {
            var menuItem = LiveEdit.ui.contextmenu.menuitem;
            var parentMenuItem = new menuitem.ParentMenuItem(this);
            var detailsMenuItem = new menuitem.DetailsMenuItem(this);
            var insertMenuItem = new menuitem.InsertMenuItem(this);
            var resetMenuItem = new menuitem.ResetMenuItem(this);
            var clearMenuItem = new menuitem.EmptyMenuItem(this);
            var openContentMenuItem = new menuitem.OpenContentMenuItem(this);
            var viewMenuItem = new menuitem.ViewMenuItem(this);
            var editMenuItem = new menuitem.EditMenuItem(this);
            var removeMenuItem = new menuitem.RemoveMenuItem(this);
            var i,
                menuItemsPlaceholder:JQuery = this.getMenuItemsPlaceholderElement();

            for (i = 0; i < this.buttons.length; i++) {
                this.buttons[i].appendTo(menuItemsPlaceholder);
            }
        }

        private updateMenuItemsForComponent(component:LiveEdit.component.Component):void {

            var buttonArray = component.getComponentType().getContextMenuConfig();

            var buttons = this.getButtons();

            var i;
            for (i = 0; i < buttons.length; i++) {
                var button:JQuery = buttons[i].getEl();
                var id:string = button.attr('data-live-edit-ui-cmp-id');
                var subStr:string = id.substring(id.lastIndexOf('-') + 1, id.length);
                if (buttonArray.indexOf(subStr) > -1) {
                    button.show(null);
                } else {
                    button.hide(null);
                }
            }
        }

        private updateTitleBar(component:LiveEdit.component.Component):void {
            this.setIcon(component);
            this.setTitle(component.getName());
        }

        private setTitle(titleText:string):void {
            this.getTitleElement().text(titleText);
        }

        private setIcon(component:LiveEdit.component.Component):void {
            var iconContainer:JQuery = this.getIconContainerElement(),
                iconCls:string = component.getComponentType().getIconCls();

            iconContainer.children('div').attr('class', iconCls);
            iconContainer.attr('title', component.getComponentType().getName());
        }

        private handleWindowResize():void {
            // fixme: improve!
            if (this.selectedComponent) {
                var dimensions:ElementDimensions = this.selectedComponent.getElementDimensions(),
                    x = dimensions.left + dimensions.width / 2 - this.getEl().width() / 2,
                    y = this.getEl().offset().top;

                this.moveToXY(x, y);
            }
        }

        private getButtons():any[] {
            return this.buttons;
        }

        private getIconContainerElement():JQuery {
            return $('.live-edit-context-menu-title-icon-container', this.getEl());
        }

        private getTitleElement():JQuery {
            return $('.live-edit-context-menu-title-text', this.getEl());
        }

        private getCloseButton():JQuery {
            return $('.live-edit-context-menu-title-close-button', this.getEl());
        }

        private getMenuItemsPlaceholderElement():JQuery {
            return $('.live-edit-context-menu-items', this.getEl());
        }

    }
}