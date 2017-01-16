module api.liveedit {

    export class SelectedHighlighter extends Highlighter {

        private static SELECT_INSTANCE: SelectedHighlighter;

        constructor() {
            super(HighlighterMode.CROSSHAIR);
        }

        public static get(): SelectedHighlighter {
            if (!SelectedHighlighter.SELECT_INSTANCE) {
                SelectedHighlighter.SELECT_INSTANCE = new SelectedHighlighter();
            }
            return SelectedHighlighter.SELECT_INSTANCE;
        }

        protected preProcessStyle(style: api.liveedit.HighlighterStyle, isEmptyView: boolean): api.liveedit.HighlighterStyle {
            return {
                stroke: 'rgba(11, 104, 249, 1)',
                strokeDasharray: style.strokeDasharray,
                fill: isEmptyView ? 'transparent' : 'rgba(90, 148, 238, .2)' // Don't use fill on empty components
            };
        }
    }
}
