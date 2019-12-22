# Intent



### ACTION_SEND

android利用 ACTION_SEND 实现简单分享功能。

##### 示例

```java
/**
* 分享文字内容
* 
* @param dlgTitle
* 	分享对话框标题
* @param subject
*	主题
* @param content
* 	分享内容（文字）
*/
private void shareText(String dlgTitle, String subject, String content) {
	if (content == null || "".equals(content)) {
		return;
	}
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    if (subject != null && !"".equals(subject)) {
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    }
    
    intent.putExtra(Intent.EXTRA_TEXT, content);
    // 设置弹出框标题
    if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
        startActivity(Intent.createChooser(intent, dlgTitle));
    } else { // 系统默认标题
        startActivity(intent);
    }
}

/**
* 分享图片和文字内容
* 
* @param dlgTitle
* 	分享对话框标题
* @param subject
*	主题
* @param content
* 	分享内容（文字）
* @param uri
* 	图片资源URI
*/
private void shareImg(String dlgTitle, String subject, String content,Uri uri) {
    if (uri == null) {
        return;
    }
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    if (subject != null && !"".equals(subject)) {
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    }
    if (content != null && !"".equals(content)) {
        intent.putExtra(Intent.EXTRA_TEXT, content);
    }
    // 设置弹出框标题
    if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
        startActivity(Intent.createChooser(intent, dlgTitle));
    } else { // 系统默认标题
        startActivity(intent);
    }
}
```

