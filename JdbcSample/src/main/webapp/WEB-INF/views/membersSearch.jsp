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
			<form:form modelAttribute="membersSearchModel">
				<div class="form-row">
				検索条件を指定する場合は「ID」または「氏名」のいずれかを入力してください
				</div>
				<div class="form-row">
					<label for="id">ID</label>
					<form:input path="id"/>
					<label for="name">氏名</label>
					<form:input path="name"/>
					<input type="submit" value="検索する" class="btn">
				</div>
				<div class="form-row errors">
					<c:out value="${message }" />
				</div>
			</form:form>
			<c:if test="${!empty membersList }">
				<table>
					<tr>
						<th>ID</th>
						<th>氏名</th>
						<th>Email</th>
						<th>電話番号</th>
						<th>誕生日</th>
						<th>&nbsp;</th>
						<th>&nbsp;</th>
					</tr>
					<form:form modelAttribute="membersSearchModel">
					<c:forEach var="members" items="${membersList }">
						<tr>
							<td><c:out value="${members.id }" /></td>
							<td><c:out value="${members.name }" /></td>
							<td><c:out value="${members.email }" /></td>
							<td><c:out value="${members.phoneNumber }" /></td>
							<td><c:out value="${members.birthday }" /></td>
							<td>
								<form action="delete" method="get">
								<input type="hidden" name="id" value="${members.id }">
								<input type="submit" value="削除">
								</form>
							</td>
							<td>
								<form action="update" method="get">
								<input type="hidden" name="id" value="${members.id }">
								<input type="submit" value="変更">
								</form>
							</td>
						</tr>
					</c:forEach>
					</form:form>
				</table>
			</c:if>
		</main>
		<jsp:include page="footer.jsp"></jsp:include>
	</div>
</body>
</html>