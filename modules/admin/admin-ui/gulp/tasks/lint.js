const CONFIG = require('../config');
const gulp = require('gulp');
const tslint = require('gulp-tslint');
const path = require('path');

const tsFilePattern = root => [`${root}/**/*.ts`, `!${root}/**/*.d.ts`];

const src = [
    ...tsFilePattern(CONFIG.root.src),
    ...tsFilePattern(CONFIG.spec.src),
];

const configuration = path.resolve('../../../tslint.json');

gulp.task('lint', () =>
    gulp.src(src, { base: './' })
        .pipe(tslint({
            formatter: 'prose',
            configuration,
        }))
        .pipe(tslint.report({
            emitError: false,
        }))
);
