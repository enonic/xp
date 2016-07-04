import through from "through";
import File from "vinyl";
import path from "path";
import fs from "fs";

export default (filePath) => through(function () {
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