/*
 Tasks are split into the several files in `tasks` folder.
 */

var requireDir = require("require-dir");
var CONFIG = require("./gulp/config");

requireDir(CONFIG.gulpTasks, {recurse: true});
