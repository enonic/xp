interface ComponentsJson {
    totalCount:number;
    componentGroups:ComponentGroup[];
}

interface ComponentGroup {
    name:string;
    components:Component[];
}

interface Component {
    key:string;
    type:string;
    name:string;
    subtitle:string;
    icon:string;
}

module LiveEdit.ui {
    var $ = $liveedit;

    export class ComponentBar extends LiveEdit.ui.Base {

        private BAR_WIDTH:number;
        private TOGGLE_WIDTH:number;
        private INNER_WIDTH:number;
        private hidden:Boolean;

        constructor() {
            super();
            this.BAR_WIDTH = 235;
            this.TOGGLE_WIDTH = 30;
            this.INNER_WIDTH = this.BAR_WIDTH - this.TOGGLE_WIDTH;
            this.hidden = true;

            this.addView();
            this.loadComponentsData();
            this.registerGlobalListeners();
            this.registerEvents();

            console.log('ComponentBar instantiated. Using jQuery ' + $().jquery);
        }


        getComponentsDataUrl():string {
            return '../../../admin2/live-edit/data/mock-components.json';
        }

        addView() {
            var html = '';
            html += '<div class="live-edit-components-container live-edit-collapsed" style="width:' + this.BAR_WIDTH + 'px; right: -' + this.INNER_WIDTH + 'px">';
            html += '    <div class="live-edit-toggle-components-container" style="width:' + this.TOGGLE_WIDTH + 'px"><span class="live-edit-toggle-text-container">Toolbar</span></div>';
            html += '        <div class="live-edit-components">';
            html += '            <div class="live-edit-form-container">';
            html += '               <form onsubmit="return false;">';
            html += '                   <input type="text" placeholder="Filter" name="filter"/>';
            html += '               </form>';
            html += '            </div>';
            html += '            <ul>';
            html += '            </ul>';
            html += '        </div>';
            html += '    </div>';
            html += '</div>';

            this.createElement(html);
            this.appendTo($('body'));
        }

        registerGlobalListeners():void {
            $(window).on('component.onSelect component.onDragStart component.onSortStart', () => {
                this.fadeOut();
            });

            $(window).on('component.onDeselect component.onDragStop component.onSortStop component.onSortUpdate component.onRemove', (event:JQueryEventObject, triggerConfig) => {
                this.fadeIn(triggerConfig);
            });
        }

        registerEvents():void {
            this.getToggle().click(() => {
                this.toggle();
            });

            this.getFilterInput().on('keyup', () => {
                this.filterList($(this).val());
            });

            // Is this ever triggered?
            this.getBar().on('mouseover', () => {
                $(window).trigger('componentBar:mouseover');
            });
        }

        loadComponentsData():void {
            $.getJSON(this.getComponentsDataUrl(), null, (data, textStatus, jqXHR) => {
                this.renderComponents(data);
                $(window).trigger('componentBar.dataLoaded');
            });
        }

        renderComponents(jsonData:ComponentsJson):void {
            var groups = jsonData.componentGroups;
            $.each(groups, (index, group:ComponentGroup) => {
                this.addHeader(group);
                if (group.components) {
                    this.addComponentsToGroup(group.components)
                }
            });
        }

        addHeader(componentGroup:ComponentGroup):void {
            var html = '';

            html += '<li class="live-edit-component-list-header">';
            html += '    <span>' + componentGroup.name + '</span>';
            html += '</li>';
            this.getComponentsContainer().append(html);
        }

        addComponentsToGroup(components:Component[]) {
            $.each(components, (index, component:Component) => {
                this.addComponent(component);
            });
        }

        addComponent(component:Component):void {
            var html = '';

            html += '<li class="live-edit-component" data-live-edit-component-key="' + component.key + '" data-live-edit-component-name="' + component.name + '" data-live-edit-component-type="' + component.type + '">';
            html += '    <img src="' + component.icon + '"/>';
            html += '    <div class="live-edit-component-text">';
            html += '        <div class="live-edit-component-text-name">' + component.name + '</div>';
            html += '        <div class="live-edit-component-text-subtitle">' + component.subtitle + '</div>';
            html += '    </div>';
            html += '</li>';
            this.getComponentsContainer().append(html);
        }

        filterList(value):void {
            var $element,
                name,
                valueLowerCased = value.toLowerCase();

            var list = this.getComponentList();

            list.each((index) => {
                $element = list[index];
                name = $element.data('live-edit-component-name').toLowerCase();
                $element.css('display', name.indexOf(valueLowerCased) > -1 ? '' : 'none');
            });
        }

        toggle():void {
            if (this.hidden) {
                this.show();
                this.hidden = false;
            } else {
                this.hide();
                this.hidden = true;
            }
        }

        show():void {
            var $bar = this.getBar();
            $bar.css('right', '0');
            this.getToggleTextContainer().text('');
            $bar.removeClass('live-edit-collapsed');
        }

        hide():void {
            var $bar = this.getBar();
            $bar.css('right', '-' + this.INNER_WIDTH + 'px');
            this.getToggleTextContainer().text('Toolbar');
            $bar.addClass('live-edit-collapsed');
        }


        fadeIn(triggerConfig):void {
            // componenttip/menu.js triggers a component.onDeselect event
            // which results in that the bar is faded in (see the listeners above)
            // The triggerConfig is a temporary workaround until we get this right.
            if (triggerConfig && triggerConfig.showComponentBar === false) {
                return;
            }
            this.getBar().fadeIn(120);
        }

        fadeOut():void {
            this.getBar().fadeOut(120);
        }

        getBar():JQuery {
            return this.getRootEl();
        }

        getToggle():JQuery {
            return $('.live-edit-toggle-components-container', this.getRootEl());
        }

        getFilterInput():JQuery {
            return $('.live-edit-form-container input[name=filter]', this.getRootEl());
        }

        getComponentsContainer():JQuery {
            return $('.live-edit-components ul', this.getRootEl());
        }

        getComponentList():JQuery {
            return $('.live-edit-component', this.getRootEl());
        }

        getToggleTextContainer():JQuery {
            return $('.live-edit-toggle-text-container', this.getRootEl());
        }

    }
}