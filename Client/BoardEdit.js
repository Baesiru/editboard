document.addEventListener("DOMContentLoaded", function () {
    const boardForm = document.getElementById('boardForm');
    const passwordForm = document.getElementById('passwordForm');
    const passwordCheckBtn = document.getElementById('passwordCheckBtn');
    const backToPost = document.getElementById('backToPost');
    
    // URL에서 boardId 추출
    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get('id');  // 게시글 ID 가져오기
    backToPost.href = `BoardDetail.html?id=${boardId}`
    if (boardId) {
        // 게시글 상세 정보 API 요청
        fetch(`http://127.0.0.1:8080/api/board/${boardId}`)
            .then(response => response.json())
            .then(data => {
                if (data) {
                    document.getElementById('title').value = data.title;
                    editor.setHTML(data.content);
                } else {
                    alert("게시글을 불러오는 데 실패했습니다.");
                    window.location.href = 'Board.html';
                }
            })
            .catch(error => {
                console.error("게시글을 불러오는 중 오류 발생:", error);
                alert("게시글을 불러오는 중 오류가 발생했습니다.");
                window.location.href = 'Board.html';
            });
    } else {
        alert("게시글 ID가 없습니다.");
        window.location.href = 'Board.html';
    }

    const editor = new toastui.Editor({
        el: document.querySelector('#content'),
        height: '400px',
        initialEditType: 'wysiwyg',
        initialValue: '',
        previewStyle: 'vertical',
        usageStatistics: false,
        hideModeSwitch: true,
        hooks: {
            addImageBlobHook: (blob, callback) => {
                const formData = new FormData();
                console.log("이미지 저장 발생");
                formData.append('file', blob);

                fetch('http://127.0.0.1:8080/api/image', {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data && data.filename) {
                        let url = "http://127.0.0.1:8080/uploads/";
                        url += data.filename;
                        callback(url, '사진 대체 텍스트 입력');
                    } else {
                        callback('image_load_fail', '사진 대체 텍스트 입력');
                    }
                })
                .catch(error => {
                    console.error('Error uploading image:', error);
                    callback('image_load_fail', '사진 대체 텍스트 입력');
                });
            }
        }
    });

    passwordCheckBtn.addEventListener('click', function (event) {
        const password = document.getElementById('password').value;
        
        if (!password) {
            alert('비밀번호를 입력해주세요.');
            return;
        }

        // 비밀번호 확인 요청
        fetch(`http://127.0.0.1:8080/api/board/${boardId}/check`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ password: password })
        })
        .then(response => {
            if (response.ok) {
                passwordForm.style.display = 'none';
                boardForm.style.display = 'block';
            } else {
                alert("비밀번호가 일치하지 않습니다.");
            }
        })
        .catch(error => {
            console.error("비밀번호 확인 중 오류 발생:", error);
            alert("비밀번호 확인 중 오류가 발생했습니다.");
        });
    });

    boardForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const title = document.getElementById('title').value;
        const content = editor.getHTML();
        const password = document.getElementById('password').value;
        
        if (!title || !content.trim()) {
            alert('모든 필드를 채워주세요.');
            return;
        }

        const boardData = {
            title: title,
            content: content,
            password: password
        };

        fetch(`http://127.0.0.1:8080/api/board/${boardId}/update`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(boardData)
        })
        .then(response => {
            if (response.ok) {
                alert('게시글이 수정되었습니다.');
                window.location.href = `BoardDetail.html?id=${boardId}`;
            } else {
                alert('게시글 수정에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error("Error submitting board data:", error);
            alert("게시글 수정에 오류가 발생했습니다.");
        });
    });
});
