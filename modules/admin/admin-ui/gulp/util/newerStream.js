var through = require("through");
var File = require("vinyl");
var path = require("path");
var fs = require("fs");

module.exports = function (filePath) {
    return through(function () {
        // If any files get through newer, just return the one entry
        this.queue(new File({
            base: path.dirname(filePath),
            path: filePath,
            contents: new Buffer(fs.readFileSync(filePath))
        }));

        // End stream by passing null to queue
        // and ignore any other additional files
        this.queue(null);
    });
};
