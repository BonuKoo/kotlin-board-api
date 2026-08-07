<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="게시글을 찾을 수 없습니다" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="not-found">
    <p class="code">404</p>
    <h1>게시글을 찾을 수 없습니다</h1>
    <p class="detail"><c:out value="${message}" /></p>
    <a class="btn btn-primary" href="${ctx}/boards">목록으로</a>
</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
