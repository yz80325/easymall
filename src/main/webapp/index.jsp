<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<html>
<body ng-app="">
<script type="text/javascript" src="webjars/angularjs/1.4.6/angular.js"></script>
<input type="text" ng-model="username">
<p>您输入的内容是：<span>{{username}}</span></p>
    上传文件
<form action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
<input type="file" name="upload_file"/>
<input type="submit" value="上传文件"/>
    </form><br>

富文本上传
    <form action="/manage/product/richtext_img_text.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file"/>
        <input type="submit" value="图片上传"/>
</form>
</body>
</html>
