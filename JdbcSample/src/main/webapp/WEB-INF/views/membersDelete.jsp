<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>JDBC Sample</title>
<link rel="stylesheet" href="/jdbc/resources/styles.css">
</head>
<body>
	<div class = "container">
		<jsp:include page="header.jsp"></jsp:include>
		<main>
		<c:if test="${not empty confirmMessage }">
			<p class="message">
				<c:out value="${confirmMessage }" />
			</p>
			<form:form modelAttribute="membersSearchModel">
			<input type="submit" value="削除" class="btn">
			</form:form>
		</c:if>
		<c:if test="${not empty completeMessage }">
			<p class="message">
				<c:out value="${completeMessage }" />
			</p>
		</c:if>
		<p>
			<a href="search">会員検索ページ</a>
		</main>
	<jsp:include page="footer.jsp"></jsp:include>
	</div>

</body>
</html>