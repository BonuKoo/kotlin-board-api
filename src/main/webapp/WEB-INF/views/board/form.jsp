<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${editing ? '게시글 수정' : '글쓰기'}" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<%-- 등록과 수정이 같은 폼을 쓴다. 보내는 곳만 다르다.
     c:url 은 컨텍스트 경로를 알아서 붙여 준다. --%>
<c:choose>
    <c:when test="${editing}">
        <c:url var="action" value="/boards/${board.id}/edit" />
    </c:when>
    <c:otherwise>
        <c:url var="action" value="/boards" />
    </c:otherwise>
</c:choose>

<h1>${pageTitle}</h1>

<form class="board-form" method="post" action="${action}">
    <label>
        <span>제목</span>
        <input type="text" name="title" value="<c:out value='${board.title}' />" required maxlength="100">
    </label>

    <label>
        <span>작성자</span>
        <c:choose>
            <c:when test="${editing}">
                <%-- 수정에서는 작성자를 바꾸지 않는다. 서버도 이 값을 무시한다. --%>
                <input type="text" value="<c:out value='${board.name}' />" readonly>
            </c:when>
            <c:otherwise>
                <input type="text" name="name" value="<c:out value='${board.name}' />" required maxlength="30">
            </c:otherwise>
        </c:choose>
    </label>

    <label>
        <span>내용</span>
        <textarea name="content" rows="12" required><c:out value="${board.content}" /></textarea>
    </label>

    <div class="actions">
        <button type="submit" class="btn btn-primary">${editing ? '수정' : '등록'}</button>
        <c:choose>
            <c:when test="${editing}">
                <a class="btn" href="${ctx}/boards/${board.id}">취소</a>
            </c:when>
            <c:otherwise>
                <a class="btn" href="${ctx}/boards">취소</a>
            </c:otherwise>
        </c:choose>
    </div>
</form>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
