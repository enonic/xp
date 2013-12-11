<!DOCTYPE html>
<html>
<head>
    <title>${status.statusCode} ${title}</title>
    <style>
        html, body, pre {
            margin: 0;
            padding: 0;
            font-family: Monaco, 'Lucida Console', monospace;
            background: #ECECEC;
        }

        h1 {
            margin: 0;
            background: #A31012;
            padding: 20px 45px;
            color: #fff;
            text-shadow: 1px 1px 1px rgba(0, 0, 0, .3);
            border-bottom: 1px solid #690000;
            font-size: 28px;
        }

        p#detail {
            margin: 0;
            padding: 15px 45px;
            background: #F5A0A0;
            border-top: 4px solid #D36D6D;
            color: #730000;
            text-shadow: 1px 1px 1px rgba(255, 255, 255, .3);
            font-size: 14px;
            border-bottom: 1px solid #BA7A7A;
        }

        p#detail.pre {
            white-space: pre;
            font-size: 13px;
            overflow: auto;
        }

        p#detail input {
            background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#AE1113), to(#A31012));
            border: 1px solid #790000;
            padding: 3px 10px;
            text-shadow: 1px 1px 0 rgba(0, 0, 0, .5);
            color: white;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
            margin: 0 10px;
            display: inline-block;
            position: relative;
            top: -1px;
        }

        h2 {
            margin: 0;
            padding: 5px 45px;
            font-size: 12px;
            background: #333;
            color: #fff;
            text-shadow: 1px 1px 1px rgba(0, 0, 0, .3);
            border-top: 4px solid #2a2a2a;
        }

        pre {
            margin: 0;
            border-bottom: 1px solid #DDD;
            text-shadow: 1px 1px 1px rgba(255, 255, 255, .5);
            position: relative;
            font-size: 12px;
            overflow: hidden;
        }

        pre span.line {
            text-align: right;
            display: inline-block;
            padding: 5px 5px;
            width: 30px;
            background: #D6D6D6;
            color: #8B8B8B;
            text-shadow: 1px 1px 1px rgba(255, 255, 255, .5);
            font-weight: bold;
        }

        pre span.code {
            padding: 5px 5px;
            position: absolute;
            right: 0;
            left: 40px;
        }

        pre:first-child span.code {
            border-top: 4px solid #CDCDCD;
        }

        pre:first-child span.line {
            border-top: 4px solid #B6B6B6;
        }

        pre.error span.line {
            background: #A31012;
            color: #fff;
            text-shadow: 1px 1px 1px rgba(0, 0, 0, .3);
        }

        pre.error {
            color: #A31012;
        }

        pre.error span.marker {
            background: #A31012;
            color: #fff;
            text-shadow: 1px 1px 1px rgba(0, 0, 0, .3);
        }
    </style>
</head>
<body>
<h1>${status.statusCode} ${title}</h1>

<p id="detail">${description}</p>

<#if source??>
<h2>
    In ${source.path} at line ${source.line}.
</h2>

<div id="source-code">
    <#list source.lines as item>
        <#assign num = item_index + source.fromLine>
        <#if num == source.line>
            <pre class="error"><span class="line">${num}</span><span class="code">${item}</span></pre>
        <#else>
            <pre><span class="line">${num}</span><span class="code">${item}</span></pre>
        </#if>
    </#list>
</div>
<#elseif exception??>
<h2>
    Here is the stack trace:
</h2>

<div>
    <#list exception.trace as item>
        <pre><span class="line">&nbsp;</span><span class="code"> ${item}</span></pre>
    </#list>
</div>
</#if>

</body>
</html>
