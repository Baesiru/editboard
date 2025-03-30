document.addEventListener("DOMContentLoaded", function () {
    const boardForm = document.getElementById('boardForm');
    
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

    boardForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const title = document.getElementById('title').value;
        const content = editor.getHTML();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        console.log(content);

        if (!title || !content.trim() || !username || !password) {
            alert('모든 필드를 채워주세요.');
            return;
        }

        const boardData = {
            title: title,
            content: content,
            username: username,
            password: password
        };

        fetch('http://127.0.0.1:8080/api/board', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(boardData)
        })
        .then(response => {
            if (response.ok) {
                alert('게시글이 작성되었습니다.');
                window.location.href = 'Board.html';
            } else {
                alert('게시글 작성에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error("Error submitting board data:", error);
            alert("게시글 작성에 오류가 발생했습니다.");
        });
    });
});
