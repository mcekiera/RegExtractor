# RegExtractor
<p align="justify"><b>Java regular expression visualization.</b><br> 
Provides a tolls to create and analyze a regular expression, its matching, structure, split effects, and capturing groups information. 
The analysis is based on color highlighting of matching of particular regular expression constructs withing given pattern. 
However, as regular expressions are quite complex topic, its could show wrong results when confronted with more complicated
patterns, especially with nested quantifiers, lookahead and lookbehind constructs.

<i><br><br>One of my best learning experiences. The project serves as introduction to regular expression topic, and what I had learned during coding, exceeded my expectations. I couldn't imagine better way to learn and understand how regex works, especially when connected with lecture of <b>Mastering Regular Expression</b> by <b>Jeffrey Friedl</b>. Personally, I would recommend such project for everyone who would like to learn regular expressions.</i></p>

<b>Instruction:</b>

<p align="justify"><br><b>1. Structure:</b><br>
<br><b>1.1. Input field</b> - top text field is for user regular expression input.
<br><b>1.2. Matching area</b> - central text area is for users text for matching. With filled input field it will highlight fragments of text wich are matched by given regular expression. Color of highlight are choosed randomly. The match is always <i>global</i>, that means that it will always show all matched fragments.
<br><b>1.3. Mode options</b> - right top of window, options for <i>case insensitive</i> and <i>multiline</i> match. 
<br><b>1.4. Building tool</b> - list of common regular expression constructs, with description field below:
- single click - display of item description in description filed;
- double click - insert construct into <i>input field</i>;

<br><b>1.5. Analysis tools tab:</b>
- Analysis tab: list for matched fragments, with two filds for diplsaying pattern - match relation,
- Split tab: display of result array after String.split() method with given pattern on particular text,
- Explain tab: display a tree-like description of all construct within given pattern,
- Groups tab: display a fragments of text, captured by grouping constructs withing particular match

<br><b>1.6. Status bar</b> - on bottom of window, display messages for user.

</p>

