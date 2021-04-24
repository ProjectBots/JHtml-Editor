# JHtml-Editor
Html Editor for Java Applications


Have you always wanted an editor that shows you what an html page looks like in a JLabel in java?  
Then this little program is for you!  

features:
- adjustable autocomplete
- real time outcome


for images:  
Since images needs to be called with their full path, I recommend that you include a function  
in your project that loads and inserts the path into the html text  
(for this to work, the image has to be inside the jar/src-path)

```java
	public static String htmlTestImg(String text) {
		StringBuilder sb = new StringBuilder(text);
		int index = 0;
		while(true) {
			index = sb.indexOf("<img src=\"", index) + 10;
			if(index == 9) break;
			int lin = sb.indexOf("\"", index);
			if(lin != -1) {
				URL src = Editor.class.getResource("/" + sb.substring(index, lin));
				if(src == null) continue;
				sb.replace(index, lin, src.toString());
			}
		}
		return sb.toString();
	}
```
example (image included):

```
<img src="test.png" width="600" height="500">
```

// TODO
- Debug function

