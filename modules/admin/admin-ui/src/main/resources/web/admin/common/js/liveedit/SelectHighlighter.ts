module api.liveedit {

    export class SelectHighlighter extends Highlighter {

        private static SELECT_INSTANCE: SelectHighlighter;

        constructor() {
            super();
        }

        public static get(): SelectHighlighter {
            if (!SelectHighlighter.SELECT_INSTANCE) {
                SelectHighlighter.SELECT_INSTANCE = new SelectHighlighter();
            }
            return SelectHighlighter.SELECT_INSTANCE;
        }


        protected preProcessStyle(style: api.liveedit.HighlighterStyle): api.liveedit.HighlighterStyle {
            return {
                stroke: '#167494',
                strokeDasharray: style.strokeDasharray,
                fill: style.fill
            };
        }
    }
}
