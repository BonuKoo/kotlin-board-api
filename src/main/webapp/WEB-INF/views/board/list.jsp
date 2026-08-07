<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="게시글 목록" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="page-head">
    <h1>게시글 목록</h1>
    <a class="btn btn-primary" href="${ctx}/boards/new">글쓰기</a>
</div>

<c:choose>
    <c:when test="${empty boards}">
        <p class="empty">아직 등록된 게시글이 없습니다.</p>
    </c:when>
    <c:otherwise>
        <table class="board-table">
            <thead>
            <tr>
                <th class="col-id">번호</th>
                <th>제목</th>
                <th class="col-name">작성자</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="board" items="${boards}">
                <tr>
                    <td class="col-id">${board.id}</td>
                    <td>
                        <a href="${ctx}/boards/${board.id}"><c:out value="${board.title}" /></a>
                    </td>
                    <td class="col-name"><c:out value="${board.name}" /></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <p class="count">총 ${fn:length(boards)}건</p>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
