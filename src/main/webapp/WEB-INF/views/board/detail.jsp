<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${board.title}" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<article class="board-detail">
    <h1><c:out value="${board.title}" /></h1>
    <p class="meta">
        <span>작성자 <c:out value="${board.name}" /></span>
        <span>·</span>
        <span>글번호 ${board.id}</span>
    </p>
    <div class="content"><c:out value="${board.content}" /></div>
</article>

<div class="actions">
    <a class="btn" href="${ctx}/boards">목록</a>
    <a class="btn" href="${ctx}/boards/${board.id}/edit">수정</a>
    <%-- 삭제는 상태를 바꾸므로 링크가 아니라 POST 폼으로 보낸다. --%>
    <form method="post" action="${ctx}/boards/${board.id}/delete"
          onsubmit="return confirm('이 게시글을 삭제할까요?');">
        <button type="submit" class="btn btn-danger">삭제</button>
    </form>
</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
