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


        protected preProcessStyle(style: api.liveedit.HighlighterStyle): api.liveedit.HighlighterStyle {
            return {
                stroke: 'rgba(90, 148, 238, 1)',     //'#4294de',
                strokeDasharray: style.strokeDasharray,
                fill: 'rgba(90, 148, 238, .3)'
            };
        }
    }
}
