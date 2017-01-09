const CONFIG = require('../config');
const gulp = require('gulp');
const tslint = require('gulp-tslint');
const path = require('path');

const src = [`${CONFIG.root.src}/**/*.ts`, `!${CONFIG.root.src}/**/*.d.ts`];
const configuration = path.resolve('tslint.json');

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
