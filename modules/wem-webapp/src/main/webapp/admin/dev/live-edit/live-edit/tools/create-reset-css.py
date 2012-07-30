#!/usr/bin/env python
import re

file = '../css/live-edit.css'

f=open(file)

resetCss = ""

for line in f:
    match = re.search('^(.+)(\s{)', line)
    if match:
        resetCss += match.group(1) + ",\n"

print "*******************************************"
print "TODO: Remove *, a and [data-live-edit-type]"
print "*******************************************\n"

# Simple remove last comma (,) and whitespace/newline from the string
resetCss = resetCss[:-2]

resetCss += " {\n"
resetCss += "\tmargin: 0;\n"
resetCss += "\tpadding: 0;\n"
resetCss += "\tborder: 0;\n"
resetCss += "\tfont-size: 11px;\n"
resetCss += "\tfont: 11px Arial;\n"
resetCss += "\tcolor: #000;\n"
resetCss += "\tvertical-align: baseline;\n"
resetCss += "}"
resetCss += "\n\n"

print resetCss

f.close()





