/*
 Clean the resulted files from src.
 */

import CONFIG from "../config";
import gulp from "gulp";
import del from "del";
import path from "path";
import {log, pipeError as error} from "../util/compileLogger";

function resolveCleanPaths() {
    return [path.join(CONFIG.root.src, CONFIG.tasks.clean.pattern)];
}

gulp.task('clean', (cb) => {
    const cleanPaths = resolveCleanPaths();
    const cleanDot = CONFIG.tasks.clean.cleanDot;
    return del(cleanPaths, {dot: cleanDot})
        .catch(e => error(cb, e))
        .then(files => log(`Cleaned ${ files && files.length || 0 } file(s).`));
});