module LiveEdit.component {
    export class ComponentPlaceholder extends Component {
        constructor(className: string = 'live-edit-empty-component') {
            super();
            this.addClass(className);
            this.getEl().setData('live-edit-empty-component', 'true');

            $liveEdit(this.getHTMLElement()).on('componentSelect.liveEdit', (event, name?)=> {
                this.onSelect();
            });

            $liveEdit(window).on('componentDeselect.liveEdit', (event, name?)=> {
                this.onDeselect();
            });
        }

        static fromComponent(type: LiveEdit.component.Type): ComponentPlaceholder {
            console.log('creating placeholder', type);
            var placeholder: ComponentPlaceholder;
            if (type === Type.IMAGE) {
                placeholder = new LiveEdit.component.ImagePlaceholder();
            } else if (type == Type.PART) {
                placeholder = new LiveEdit.component.PartPlaceholder();
            } else if (type == Type.LAYOUT) {
                placeholder = new LiveEdit.component.LayoutPlaceholder();
            } else if (type == Type.TEXT) {
                placeholder = new LiveEdit.component.TextPlaceholder();
            } else {
                var emptyComponentIcon = new api.dom.DivEl();
                emptyComponentIcon.addClass('live-edit-empty-component-icon');
                placeholder = new ComponentPlaceholder();
                placeholder.appendChild(emptyComponentIcon);
            }
            return placeholder;
        }

        onSelect() {

        }

        onDeselect() {

        }

    }
}