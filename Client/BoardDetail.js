document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get('id');

    if (boardId) {
        fetchBoardDetails(boardId);
        fetchComments(boardId);

        document.getElementById('submitComment').addEventListener('click', function () {
            postComment(boardId);
        });
        
        document.getElementById('editButton').addEventListener('click', function() {
            window.location.href = `BoardEdit.html?id=${boardId}`;
        });

        document.getElementById('deleteButton').addEventListener('click', function() {
            const passwordModal = new bootstrap.Modal(document.getElementById('passwordModal'));
            passwordModal.show();
            document.getElementById('confirmPassword').addEventListener('click', function () {
                const password = document.getElementById('password').value;
                if (password) {
                    fetch(`http://127.0.0.1:8080/api/board/${boardId}/delete`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ password })
                    })
                    .then(response => {
                        if (response.ok) {
                            alert("게시글이 삭제 되었습니다.");
                            window.location.href = 'Board.html';
                        } else {
                            alert("비밀번호가 틀렸습니다.");
                        }
                    })
                    .catch(error => {
                        console.error("Error deleting board:", error);
                        alert("게시글 삭제에 실패했습니다.");
                    });
                } else {
                    alert("비밀번호를 입력하세요.");
                }
            });
        });

    } else {
        alert("게시글 ID가 없습니다.");
    }
});


function fetchBoardDetails(boardId) {
    fetch(`http://127.0.0.1:8080/api/board/${boardId}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('title').textContent = data.title;
            document.getElementById('username').textContent = data.username;
            document.getElementById('createdAt').textContent = new Date(data.createdAt).toLocaleString();
            document.getElementById('content').innerHTML = data.content;
            document.getElementById('viewCount').textContent = `조회수: ${data.viewCount}`;
        })
        .catch(error => console.error("Error fetching board details:", error));
}



function fetchComments(boardId) {
    fetch(`http://127.0.0.1:8080/api/board/${boardId}/comment`)
        .then(response => response.json())
        .then(data => {
            const commentList = document.getElementById('commentList');
            commentList.innerHTML = '';

            data.forEach(comment => {
                const commentItem = document.createElement('div');
                commentItem.classList.add('comment-item');
                commentItem.id = `comment-${comment.id}`;
                commentItem.style.marginLeft = `${comment.depth * 20}px`;

                commentItem.innerHTML = `
                    <strong>${comment.username}</strong> - <span>${new Date(comment.createdAt).toLocaleString()}</span>
                    <p class="comment-content">${comment.content}</p>
                    <button class="btn btn-sm btn-warning edit-btn" onclick="checkPasswordAndEdit(${comment.id}, ${boardId})">수정</button>
                    <button class="btn btn-sm btn-danger delete-btn" data-id="${comment.id}">삭제</button>
                    <div id="replyForm-${comment.id}" class="reply-form" style="display: none;">
                        <input type="text" class="form-control mb-2" placeholder="이름" id="replyUsername-${comment.id}">
                        <input type="password" class="form-control mb-2" placeholder="비밀번호" id="replyPassword-${comment.id}">
                        <textarea class="form-control mb-2" placeholder="답글을 입력하세요" id="replyContent-${comment.id}"></textarea>
                        <button class="btn btn-primary" onclick="submitReply(${boardId}, ${comment.id})">답글 작성</button>
                    </div>
                `;

                commentList.appendChild(commentItem);
            });

            document.querySelectorAll('.reply-btn').forEach(btn => {
                btn.addEventListener('click', function () {
                    const parentId = this.getAttribute('data-id');
                    toggleReplyForm(parentId);
                });
            });

            document.querySelectorAll('.delete-btn').forEach(btn => {
                btn.addEventListener('click', function () {
                    const commentId = this.getAttribute('data-id');
                    deleteComment(boardId, commentId);
                });
            });
        })
        .catch(error => console.error("Error fetching comments:", error));
}

function toggleReplyForm(parentId) {
    document.querySelectorAll('.reply-form').forEach(form => {
        form.style.display = 'none';
    });

    const replyForm = document.getElementById(`replyForm-${parentId}`);
    replyForm.style.display = replyForm.style.display === 'none' ? 'block' : 'none';
}

function postComment(boardId) {
    const content = document.getElementById('commentContent').value;
    const username = document.getElementById('commentUsername').value;
    const password = document.getElementById('commentPassword').value;

    if (!content || !username || !password) {
        alert("모든 필드를 입력하세요.");
        return;
    }

    fetch(`http://127.0.0.1:8080/api/board/${boardId}/comment`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content, username, password })
    })
    .then(response => {
        if (response.ok) {
            alert("댓글이 작성되었습니다.");
            fetchComments(boardId);
        } else {
            alert("댓글 작성에 실패했습니다.");
        }
    })
    .catch(error => console.error("Error posting comment:", error));
}

function submitReply(boardId, parentId) {
    const content = document.getElementById(`replyContent-${parentId}`).value;
    const username = document.getElementById(`replyUsername-${parentId}`).value;
    const password = document.getElementById(`replyPassword-${parentId}`).value;

    if (!content || !username || !password) {
        alert("모든 필드를 입력하세요.");
        return;
    }

    fetch(`http://127.0.0.1:8080/api/board/${boardId}/comment`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content, username, password, parentId })
    })
    .then(response => {
        if (response.ok) {
            alert("답글이 작성되었습니다.");
            fetchComments(boardId);
        } else {
            alert("답글 작성에 실패했습니다.");
        }
    })
    .catch(error => console.error("Error posting reply:", error));
}

function deleteComment(boardId, commentId) {
    const password = prompt("비밀번호를 입력하세요:");

    if (!password) {
        alert("비밀번호를 입력해야 합니다.");
        return;
    }

    fetch(`http://127.0.0.1:8080/api/comment/${boardId, commentId}/delete`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password })
    })
    .then(response => {
        if (response.ok) {
            alert("댓글이 삭제되었습니다.");
            fetchComments(boardId);
        } else {
            alert("비밀번호가 틀렸습니다.");
        }
    })
    .catch(error => console.error("Error deleting comment:", error));
}

function checkPasswordAndEdit(commentId, boardId) {
    const password = prompt("수정하려면 비밀번호를 입력하세요:");

    if (!password) {
        alert("비밀번호를 입력해야 합니다.");
        return;
    }

    fetch(`http://127.0.0.1:8080/api/comment/${commentId}/check`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password })
    })
    .then(response => {
        if (response.ok) {
            const commentContent = document.querySelector(`#comment-${commentId} .comment-content`);
            commentContent.style.display = 'none'; // 기존 댓글 내용 숨기기
            const commentItem = document.getElementById(`comment-${commentId}`);
            
            commentItem.innerHTML += `
                <textarea class="form-control" id="editContent-${commentId}" rows="3">${commentContent.innerHTML}</textarea>
                <button class="btn btn-primary" onclick="updateComment(${commentId}, ${password}, ${boardId})">수정하기</button>
                <button class="btn btn-secondary cancel-btn" onclick="cancelEdit(${commentId})">취소하기</button>
            `;
            
            // 수정 및 삭제 버튼 숨기기
            const editBtn = commentItem.querySelector('.edit-btn');
            const deleteBtn = commentItem.querySelector('.delete-btn');
            editBtn.style.display = 'none';
            deleteBtn.style.display = 'none';
        } else {
            alert("비밀번호가 틀렸습니다.");
        }
    })
    .catch(error => console.error("Error checking password:", error));
}


function updateComment(commentId, password, boardId) {
    const newContent = document.getElementById(`editContent-${commentId}`).value;
    console.log(password);
    fetch(`http://127.0.0.1:8080/api/comment/${commentId}/update`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password, content: newContent })
    })
    .then(response => {
        if (response.ok) {
            alert("댓글이 수정되었습니다.");
            fetchComments(boardId);
        } else {
            alert("댓글 수정에 실패했습니다.");
        }
    })
    .catch(error => console.error("Error updating comment:", error));
}

function cancelEdit(commentId) {
    const commentElement = document.getElementById(`comment-${commentId}`);
    
    // 댓글 내용과 수정 폼 처리
    const originalContent = commentElement.querySelector('.comment-content');
    originalContent.style.display = 'block'; // 원래 내용 보이기

    // 수정 버튼, 삭제 버튼 보이기
    const editBtn = commentElement.querySelector('.edit-btn');
    const deleteBtn = commentElement.querySelector('.delete-btn');
    editBtn.style.display = 'inline'; // 수정 버튼 보이기
    deleteBtn.style.display = 'inline'; // 삭제 버튼 보이기

    // 수정 폼 숨기기
    const editForm = commentElement.querySelector('.edit-form');
    if (editForm) {
        editForm.style.display = 'none'; // 수정 폼 숨기기
    }
    
    // 텍스트 영역 숨기기
    const editContent = document.getElementById(`editContent-${commentId}`);
    if (editContent) {
        editContent.style.display = 'none'; // 수정 텍스트 박스 숨기기
    }

    // 취소 버튼 숨기기
    const cancelBtn = commentElement.querySelector('.cancel-btn');
    if (cancelBtn) {
        cancelBtn.style.display = 'none'; // 취소 버튼 숨기기
    }
}
